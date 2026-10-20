package fr.miage.premiereimpression.preparation.application.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.miage.premiereimpression.preparation.application.port.in.PlanningCalcule;
import fr.miage.premiereimpression.preparation.application.port.in.PlanningRevisionUseCase;
import fr.miage.premiereimpression.preparation.application.port.out.CandidaturesConnues;
import fr.miage.premiereimpression.preparation.application.port.out.ConceptRequisRepository;
import fr.miage.premiereimpression.preparation.domain.algo.PlacementStrategyType;
import fr.miage.premiereimpression.preparation.domain.algo.PlanningDeRevision;
import fr.miage.premiereimpression.preparation.domain.exception.DonneeInvalideException;
import fr.miage.premiereimpression.preparation.domain.exception.EntretienSansDateException;
import fr.miage.premiereimpression.preparation.domain.model.Bloc;
import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequis;

/**
 * Calcule le planning jusqu'à la veille de l'entretien : {@code n = jour de l'entretien − aujourd'hui}.
 * La date du jour vient d'une horloge injectée, pour que les tests ne dépendent pas du jour où ils tournent.
 */
public class PlanningRevisionService implements PlanningRevisionUseCase {

    private final ConceptRequisRepository concepts;
    private final CandidaturesConnues candidatures;
    private final Clock horloge;

    public PlanningRevisionService(ConceptRequisRepository concepts, CandidaturesConnues candidatures, Clock horloge) {
        this.concepts = concepts;
        this.candidatures = candidatures;
        this.horloge = horloge;
    }

    @Override
    public PlanningCalcule planifier(CandidatureId candidature, PlacementStrategyType strategie,
            List<Integer> disponibilites) {
        if (strategie == null) {
            throw new DonneeInvalideException("La stratégie de placement est obligatoire");
        }
        VerificationCandidature.exiger(candidatures, candidature);
        LocalDate aujourdhui = LocalDate.now(horloge);
        LocalDate entretien = candidatures.dateEntretien(candidature)
                .orElseThrow(() -> new EntretienSansDateException(candidature));
        // entretien aujourd'hui ou passé : aucun jour disponible, planning vide (CA_vide1)
        long n = Math.max(0, ChronoUnit.DAYS.between(aujourdhui, entretien));
        if (disponibilites == null || disponibilites.size() != n) {
            throw new DonneeInvalideException("Il faut une disponibilité par jour jusqu'à la veille de l'entretien, soit "
                    + n + " ; reçu : " + (disponibilites == null ? 0 : disponibilites.size()));
        }
        List<ConceptRequis> besoin = concepts.deCandidature(candidature);
        List<Bloc> blocs = PlanningDeRevision.planning(besoin, strategie.strategie(), disponibilites);
        return presenter(aujourdhui, disponibilites, besoin, blocs);
    }

    /** Date chaque jour, et déduit des blocs le niveau atteint par chaque concept et le temps utilisé. */
    private static PlanningCalcule presenter(LocalDate premierJour, List<Integer> disponibilites,
            List<ConceptRequis> besoin, List<Bloc> blocs) {
        List<PlanningCalcule.Jour> jours = new ArrayList<>();
        for (int j = 0; j < disponibilites.size(); j++) {
            int rang = j;
            jours.add(new PlanningCalcule.Jour(premierJour.plusDays(j), disponibilites.get(j),
                    blocs.stream().filter(b -> b.jour() == rang).toList()));
        }
        Map<ConceptRequis, Integer> atteint = new HashMap<>();
        blocs.forEach(b -> atteint.merge(b.niveau().concept(), b.niveau().k(), Math::max));
        List<PlanningCalcule.NiveauAtteint> niveaux = besoin.stream()
                .map(c -> new PlanningCalcule.NiveauAtteint(c, atteint.getOrDefault(c, c.actuel())))
                .toList();
        long disponible = disponibilites.stream().mapToLong(Integer::longValue).sum();
        long utilise = blocs.stream().mapToLong(Bloc::duree).sum();
        return new PlanningCalcule(jours, niveaux, disponible, utilise);
    }
}
