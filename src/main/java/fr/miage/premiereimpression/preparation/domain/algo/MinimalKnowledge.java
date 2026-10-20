package fr.miage.premiereimpression.preparation.domain.algo;

import java.util.Comparator;

import fr.miage.premiereimpression.preparation.domain.model.ConceptRequis;

/** MKW : le concept dont le nombre de niveaux restants {@code nrᵢ} est maximal, quel que soit {@code qtnᵢ}. */
final class MinimalKnowledge implements PlacementStrategy {

    private static final Comparator<ConceptRequis> NIVEAUX_RESTANTS_DECROISSANTS =
            Comparator.comparingInt(ConceptRequis::nr).reversed();

    @Override
    public Comparator<ConceptRequis> priorite() {
        return NIVEAUX_RESTANTS_DECROISSANTS;
    }
}
