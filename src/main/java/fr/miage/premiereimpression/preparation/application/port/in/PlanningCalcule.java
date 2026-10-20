package fr.miage.premiereimpression.preparation.application.port.in;

import java.time.LocalDate;
import java.util.List;

import fr.miage.premiereimpression.preparation.domain.model.Bloc;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequis;

/**
 * Résultat d'un calcul de planning, prêt à être affiché : les jours datés avec leurs blocs,
 * le niveau atteint par chaque concept et le bilan du temps.
 */
public record PlanningCalcule(List<Jour> jours, List<NiveauAtteint> niveauxAtteints, long tempsDisponible,
        long tempsUtilise) {

    public PlanningCalcule {
        jours = List.copyOf(jours);
        niveauxAtteints = List.copyOf(niveauxAtteints);
    }

    public long tempsInutilise() {
        return tempsDisponible - tempsUtilise;
    }

    public record Jour(LocalDate date, int disponible, List<Bloc> blocs) {

        public Jour {
            blocs = List.copyOf(blocs);
        }
    }

    public record NiveauAtteint(ConceptRequis concept, int atteint) {
    }
}
