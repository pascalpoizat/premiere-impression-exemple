package fr.miage.premiereimpression.preparation.application.port.in;

import java.util.List;

import fr.miage.premiereimpression.preparation.domain.algo.PlacementStrategyType;
import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;

/** Port d'entrée : planning de révision d'une candidature, calculé à la demande. */
public interface PlanningRevisionUseCase {

    /**
     * @param disponibilites une durée en minutes pour chacun des {@code n} jours, du jour même à la veille de l'entretien
     */
    PlanningCalcule planifier(CandidatureId candidature, PlacementStrategyType strategie, List<Integer> disponibilites);
}
