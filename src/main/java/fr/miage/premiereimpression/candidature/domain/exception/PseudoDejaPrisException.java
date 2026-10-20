package fr.miage.premiereimpression.candidature.domain.exception;

public class PseudoDejaPrisException extends CandidatureException {

    public PseudoDejaPrisException(String pseudo) {
        super("Le pseudo « " + pseudo + " » est déjà pris");
    }
}
