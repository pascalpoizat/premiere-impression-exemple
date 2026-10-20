package fr.miage.premiereimpression.preparation.domain.exception;

/** Une règle métier est violée par les données fournies. */
public class DonneeInvalideException extends PreparationException {

    public DonneeInvalideException(String message) {
        super(message);
    }
}
