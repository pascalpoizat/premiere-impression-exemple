package fr.miage.premiereimpression.tenue.application.service;

import fr.miage.premiereimpression.tenue.application.port.out.ProfilsConnus;
import fr.miage.premiereimpression.tenue.domain.exception.ProfilInconnuException;
import fr.miage.premiereimpression.tenue.domain.model.ProfilId;

/** Règle commune aux services du module : on ne travaille que sur des profils existants. */
final class VerificationProfil {

    private VerificationProfil() {
    }

    static void exiger(ProfilsConnus profils, ProfilId profil) {
        if (!profils.existe(profil)) {
            throw new ProfilInconnuException(profil);
        }
    }
}
