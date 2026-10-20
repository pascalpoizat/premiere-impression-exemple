package fr.miage.premiereimpression.preparation.domain.algo;

import java.util.Comparator;

import fr.miage.premiereimpression.preparation.domain.model.ConceptRequis;

/**
 * CTA : le concept dont le reste à apprendre {@code nrᵢ · qtnᵢ} est minimal
 * (en ordonnancement : Shortest Remaining Processing Time).
 */
final class CloseToAchievement implements PlacementStrategy {

    private static final Comparator<ConceptRequis> RESTE_CROISSANT = Comparator.comparingLong(ConceptRequis::reste);

    @Override
    public Comparator<ConceptRequis> priorite() {
        return RESTE_CROISSANT;
    }
}
