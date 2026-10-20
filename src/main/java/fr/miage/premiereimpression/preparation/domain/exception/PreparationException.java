package fr.miage.premiereimpression.preparation.domain.exception;

/** Racine des erreurs métier du module preparation. */
public abstract class PreparationException extends RuntimeException {

    protected PreparationException(String message) {
        super(message);
    }
}
