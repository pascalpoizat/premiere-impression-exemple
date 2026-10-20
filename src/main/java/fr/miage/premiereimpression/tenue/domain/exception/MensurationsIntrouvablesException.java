package fr.miage.premiereimpression.tenue.domain.exception;

import fr.miage.premiereimpression.tenue.domain.model.ProfilId;

public class MensurationsIntrouvablesException extends TenueException {

    public MensurationsIntrouvablesException(ProfilId id) {
        super("Le profil " + id.valeur() + " n'a pas encore renseigné ses mensurations");
    }
}
