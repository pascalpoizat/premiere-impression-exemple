package fr.miage.premiereimpression.tenue.domain.exception;

/** Une règle métier est violée par les données fournies. */
public class DonneeInvalideException extends TenueException {

    public DonneeInvalideException(String message) {
        super(message);
    }
}
