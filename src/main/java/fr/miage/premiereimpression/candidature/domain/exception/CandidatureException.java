package fr.miage.premiereimpression.candidature.domain.exception;

/** Racine des erreurs métier du module candidature. */
public abstract class CandidatureException extends RuntimeException {

    protected CandidatureException(String message) {
        super(message);
    }
}
