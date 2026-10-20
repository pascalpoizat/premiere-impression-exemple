package fr.miage.premiereimpression.preparation.application.service;

import fr.miage.premiereimpression.preparation.application.port.out.CandidaturesConnues;
import fr.miage.premiereimpression.preparation.domain.exception.CandidatureInconnueException;
import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;

/** Règle commune aux services du module : on ne travaille que sur des candidatures existantes. */
final class VerificationCandidature {

    private VerificationCandidature() {
    }

    static void exiger(CandidaturesConnues candidatures, CandidatureId candidature) {
        if (!candidatures.existe(candidature)) {
            throw new CandidatureInconnueException(candidature);
        }
    }
}
