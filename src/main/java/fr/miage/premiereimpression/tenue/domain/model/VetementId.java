package fr.miage.premiereimpression.tenue.domain.model;

import fr.miage.premiereimpression.tenue.domain.exception.DonneeInvalideException;

public record VetementId(long valeur) {

    public VetementId {
        if (valeur <= 0) {
            throw new DonneeInvalideException("Identifiant de vêtement invalide : " + valeur);
        }
    }
}
