package fr.miage.premiereimpression.tenue.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import fr.miage.premiereimpression.tenue.domain.exception.DonneeInvalideException;
import fr.miage.premiereimpression.tenue.domain.exception.MensurationsIntrouvablesException;
import fr.miage.premiereimpression.tenue.domain.exception.ProfilInconnuException;
import fr.miage.premiereimpression.tenue.domain.exception.TenueException;

/** Traduit les erreurs métier en réponses HTTP au format RFC 9457 (application/problem+json). */
@RestControllerAdvice(basePackageClasses = TenueExceptionHandler.class)
class TenueExceptionHandler {

    @ExceptionHandler(ProfilInconnuException.class)
    ProblemDetail profilInconnu(ProfilInconnuException exception) {
        return probleme(HttpStatus.NOT_FOUND, "Profil introuvable", exception);
    }

    @ExceptionHandler(MensurationsIntrouvablesException.class)
    ProblemDetail mensurationsIntrouvables(MensurationsIntrouvablesException exception) {
        return probleme(HttpStatus.NOT_FOUND, "Mensurations introuvables", exception);
    }

    @ExceptionHandler(DonneeInvalideException.class)
    ProblemDetail donneeInvalide(DonneeInvalideException exception) {
        return probleme(HttpStatus.BAD_REQUEST, "Donnée invalide", exception);
    }

    private static ProblemDetail probleme(HttpStatus statut, String titre, TenueException exception) {
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(statut, exception.getMessage());
        probleme.setTitle(titre);
        return probleme;
    }
}
