package fr.miage.premiereimpression.tenue.domain.exception;

/** Racine des erreurs métier du module tenue. */
public abstract class TenueException extends RuntimeException {

    protected TenueException(String message) {
        super(message);
    }
}
