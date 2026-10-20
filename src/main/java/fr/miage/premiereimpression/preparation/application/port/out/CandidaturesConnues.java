package fr.miage.premiereimpression.preparation.application.port.out;

import java.time.LocalDate;
import java.util.Optional;

import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;

/** Port de sortie : les candidatures appartiennent au module candidature, on lui demande ce qu'il faut. */
public interface CandidaturesConnues {

    boolean existe(CandidatureId candidature);

    /** Vide si la candidature n'a pas (encore) de date d'entretien. */
    Optional<LocalDate> dateEntretien(CandidatureId candidature);
}
