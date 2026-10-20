package fr.miage.premiereimpression.candidature.domain.model;

import fr.miage.premiereimpression.candidature.domain.exception.DonneeInvalideException;

public record CandidatureId(long valeur) {

    public CandidatureId {
        if (valeur <= 0) {
            throw new DonneeInvalideException("Identifiant de candidature invalide : " + valeur);
        }
    }
}
