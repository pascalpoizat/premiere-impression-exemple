package fr.miage.premiereimpression.tenue.domain.model;

import fr.miage.premiereimpression.tenue.domain.exception.DonneeInvalideException;

/**
 * Référence à un profil, propre au module tenue : chaque module a ses propres types
 * (contexte délimité), le lien avec le module candidature passe par un port de sortie.
 */
public record ProfilId(long valeur) {

    public ProfilId {
        if (valeur <= 0) {
            throw new DonneeInvalideException("Identifiant de profil invalide : " + valeur);
        }
    }
}
