package fr.miage.premiereimpression.candidature.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import fr.miage.premiereimpression.candidature.domain.exception.CandidatureException;
import fr.miage.premiereimpression.candidature.domain.exception.CandidatureIntrouvableException;
import fr.miage.premiereimpression.candidature.domain.exception.DonneeInvalideException;
import fr.miage.premiereimpression.candidature.domain.exception.ProfilIntrouvableException;
import fr.miage.premiereimpression.candidature.domain.exception.PseudoDejaPrisException;

/** Traduit les erreurs métier en réponses HTTP au format RFC 9457 (application/problem+json). */
@RestControllerAdvice(basePackageClasses = CandidatureExceptionHandler.class)
class CandidatureExceptionHandler {

    @ExceptionHandler(ProfilIntrouvableException.class)
    ProblemDetail profilIntrouvable(ProfilIntrouvableException exception) {
        return probleme(HttpStatus.NOT_FOUND, "Profil introuvable", exception);
    }

    @ExceptionHandler(CandidatureIntrouvableException.class)
    ProblemDetail candidatureIntrouvable(CandidatureIntrouvableException exception) {
        return probleme(HttpStatus.NOT_FOUND, "Candidature introuvable", exception);
    }

    @ExceptionHandler(PseudoDejaPrisException.class)
    ProblemDetail pseudoDejaPris(PseudoDejaPrisException exception) {
        return probleme(HttpStatus.CONFLICT, "Pseudo déjà pris", exception);
    }

    @ExceptionHandler(DonneeInvalideException.class)
    ProblemDetail donneeInvalide(DonneeInvalideException exception) {
        return probleme(HttpStatus.BAD_REQUEST, "Donnée invalide", exception);
    }

    private static ProblemDetail probleme(HttpStatus statut, String titre, CandidatureException exception) {
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(statut, exception.getMessage());
        probleme.setTitle(titre);
        return probleme;
    }
}
