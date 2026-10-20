package fr.miage.premiereimpression.preparation.domain.model;

import fr.miage.premiereimpression.preparation.domain.exception.DonneeInvalideException;

public record ConceptRequisId(long valeur) {

    public ConceptRequisId {
        if (valeur <= 0) {
            throw new DonneeInvalideException("Identifiant de concept requis invalide : " + valeur);
        }
    }
}
