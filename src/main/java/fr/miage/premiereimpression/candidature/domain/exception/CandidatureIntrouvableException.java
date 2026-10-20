package fr.miage.premiereimpression.candidature.domain.exception;

import fr.miage.premiereimpression.candidature.domain.model.CandidatureId;

public class CandidatureIntrouvableException extends CandidatureException {

    public CandidatureIntrouvableException(CandidatureId id) {
        super("Candidature introuvable : " + id.valeur());
    }
}
