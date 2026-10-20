package fr.miage.premiereimpression.tenue.domain.exception;

import fr.miage.premiereimpression.tenue.domain.model.ProfilId;

public class ProfilInconnuException extends TenueException {

    public ProfilInconnuException(ProfilId id) {
        super("Profil introuvable : " + id.valeur());
    }
}
