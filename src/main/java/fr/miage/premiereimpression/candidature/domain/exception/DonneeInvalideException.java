package fr.miage.premiereimpression.candidature.domain.exception;

/** Une règle métier est violée par les données fournies. */
public class DonneeInvalideException extends CandidatureException {

    public DonneeInvalideException(String message) {
        super(message);
    }
}
