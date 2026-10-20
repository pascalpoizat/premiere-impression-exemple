package fr.miage.premiereimpression.preparation.domain.exception;

import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;

public class CandidatureInconnueException extends PreparationException {

    public CandidatureInconnueException(CandidatureId id) {
        super("Candidature introuvable : " + id.valeur());
    }
}
