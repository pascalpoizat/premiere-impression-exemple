package fr.miage.premiereimpression.preparation.domain.exception;

import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequisId;

public class ConceptRequisInconnuException extends PreparationException {

    public ConceptRequisInconnuException(CandidatureId candidature, ConceptRequisId concept) {
        super("Concept requis " + concept.valeur() + " introuvable pour la candidature " + candidature.valeur());
    }
}
