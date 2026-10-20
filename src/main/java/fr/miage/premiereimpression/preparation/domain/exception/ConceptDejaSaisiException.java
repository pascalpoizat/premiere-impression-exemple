package fr.miage.premiereimpression.preparation.domain.exception;

import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;

/** Un nom de concept ne peut être saisi qu'une fois par candidature. */
public class ConceptDejaSaisiException extends PreparationException {

    public ConceptDejaSaisiException(CandidatureId candidature, String nom) {
        super("Le concept « " + nom + " » est déjà saisi pour la candidature " + candidature.valeur());
    }
}
