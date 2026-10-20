package fr.miage.premiereimpression.preparation.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import fr.miage.premiereimpression.preparation.domain.exception.CandidatureInconnueException;
import fr.miage.premiereimpression.preparation.domain.exception.ConceptDejaSaisiException;
import fr.miage.premiereimpression.preparation.domain.exception.ConceptRequisInconnuException;
import fr.miage.premiereimpression.preparation.domain.exception.DonneeInvalideException;
import fr.miage.premiereimpression.preparation.domain.exception.EntretienSansDateException;
import fr.miage.premiereimpression.preparation.domain.exception.PreparationException;

/** Traduit les erreurs métier en réponses HTTP au format RFC 9457 (application/problem+json). */
@RestControllerAdvice(basePackageClasses = PreparationExceptionHandler.class)
class PreparationExceptionHandler {

    @ExceptionHandler(CandidatureInconnueException.class)
    ProblemDetail candidatureInconnue(CandidatureInconnueException exception) {
        return probleme(HttpStatus.NOT_FOUND, "Candidature introuvable", exception);
    }

    @ExceptionHandler(ConceptRequisInconnuException.class)
    ProblemDetail conceptInconnu(ConceptRequisInconnuException exception) {
        return probleme(HttpStatus.NOT_FOUND, "Concept requis introuvable", exception);
    }

    @ExceptionHandler(ConceptDejaSaisiException.class)
    ProblemDetail conceptDejaSaisi(ConceptDejaSaisiException exception) {
        return probleme(HttpStatus.CONFLICT, "Concept déjà saisi", exception);
    }

    @ExceptionHandler(EntretienSansDateException.class)
    ProblemDetail entretienSansDate(EntretienSansDateException exception) {
        return probleme(HttpStatus.CONFLICT, "Entretien sans date", exception);
    }

    @ExceptionHandler(DonneeInvalideException.class)
    ProblemDetail donneeInvalide(DonneeInvalideException exception) {
        return probleme(HttpStatus.BAD_REQUEST, "Donnée invalide", exception);
    }

    private static ProblemDetail probleme(HttpStatus statut, String titre, PreparationException exception) {
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(statut, exception.getMessage());
        probleme.setTitle(titre);
        return probleme;
    }
}
