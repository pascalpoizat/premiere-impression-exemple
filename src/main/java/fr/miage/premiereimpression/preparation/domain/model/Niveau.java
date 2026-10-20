package fr.miage.premiereimpression.preparation.domain.model;

import java.util.Objects;

import fr.miage.premiereimpression.preparation.domain.exception.DonneeInvalideException;

/** {@code Cᵢ→k} : monter le concept au niveau {@code k}, ce qui coûte {@code qtnᵢ} minutes. */
public record Niveau(ConceptRequis concept, int k) {

    public Niveau {
        Objects.requireNonNull(concept, "concept");
        // INV_BORNES_NIVEAU : on ne cible que du niveau actuel + 1 au niveau requis, inclus
        if (k <= concept.actuel() || k > concept.requis()) {
            throw new DonneeInvalideException("Niveau " + k + " hors de ]" + concept.actuel() + ", "
                    + concept.requis() + "] pour le concept « " + concept.nom() + " »");
        }
    }

    public int cout() {
        return concept.qtn();
    }
}
