package fr.miage.premiereimpression.preparation.domain.exception;

import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;

/** Le planning se calcule jusqu'à la veille de l'entretien : il faut en connaître la date. */
public class EntretienSansDateException extends PreparationException {

    public EntretienSansDateException(CandidatureId id) {
        super("La candidature " + id.valeur() + " n'a pas de date d'entretien");
    }
}
