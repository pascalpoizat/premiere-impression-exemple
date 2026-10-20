package fr.miage.premiereimpression.candidature.domain.exception;

import fr.miage.premiereimpression.candidature.domain.model.ProfilId;

public class ProfilIntrouvableException extends CandidatureException {

    public ProfilIntrouvableException(ProfilId id) {
        super("Profil introuvable : " + id.valeur());
    }
}
