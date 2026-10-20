package fr.miage.premiereimpression.candidature.domain.model;

import fr.miage.premiereimpression.candidature.domain.exception.DonneeInvalideException;

public record ProfilId(long valeur) {

    public ProfilId {
        if (valeur <= 0) {
            throw new DonneeInvalideException("Identifiant de profil invalide : " + valeur);
        }
    }
}
