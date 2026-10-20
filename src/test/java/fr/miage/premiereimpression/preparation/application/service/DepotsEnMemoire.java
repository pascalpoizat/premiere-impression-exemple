package fr.miage.premiereimpression.preparation.application.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.miage.premiereimpression.preparation.application.port.out.CandidaturesConnues;
import fr.miage.premiereimpression.preparation.application.port.out.ConceptRequisRepository;
import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequis;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequisId;
import fr.miage.premiereimpression.preparation.domain.model.NouveauConceptRequis;

/** Faux adaptateurs en mémoire : les services se testent sans base de données ni Spring. */
final class DepotsEnMemoire {

    private DepotsEnMemoire() {
    }

    static final class Concepts implements ConceptRequisRepository {

        private final List<ConceptRequis> concepts = new ArrayList<>();
        private long prochainId = 1;

        @Override
        public ConceptRequis ajouter(NouveauConceptRequis c) {
            ConceptRequis enregistre = new ConceptRequis(new ConceptRequisId(prochainId++), c.candidature(), c.nom(),
                    c.actuel(), c.requis(), c.qtn(), c.rang());
            concepts.add(enregistre);
            return enregistre;
        }

        @Override
        public List<ConceptRequis> deCandidature(CandidatureId candidature) {
            return concepts.stream().filter(c -> c.candidature().equals(candidature))
                    .sorted(Comparator.comparingInt(ConceptRequis::rang)).toList();
        }

        @Override
        public boolean nomDejaSaisi(CandidatureId candidature, String nom) {
            return deCandidature(candidature).stream().anyMatch(c -> c.nom().equals(nom));
        }

        @Override
        public int rangMax(CandidatureId candidature) {
            return deCandidature(candidature).stream().mapToInt(ConceptRequis::rang).max().orElse(0);
        }

        @Override
        public boolean supprimer(CandidatureId candidature, ConceptRequisId concept) {
            return concepts.removeIf(c -> c.candidature().equals(candidature) && c.id().equals(concept));
        }
    }

    static final class Candidatures implements CandidaturesConnues {

        private final Map<CandidatureId, Optional<LocalDate>> entretiens = new HashMap<>();

        Candidatures avec(CandidatureId candidature, LocalDate entretien) {
            entretiens.put(candidature, Optional.ofNullable(entretien));
            return this;
        }

        @Override
        public boolean existe(CandidatureId candidature) {
            return entretiens.containsKey(candidature);
        }

        @Override
        public Optional<LocalDate> dateEntretien(CandidatureId candidature) {
            return entretiens.getOrDefault(candidature, Optional.empty());
        }
    }
}
