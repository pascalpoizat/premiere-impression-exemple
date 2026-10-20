package fr.miage.premiereimpression.candidature.adapter.in.web;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import fr.miage.premiereimpression.candidature.application.port.in.CreerCandidatureCommande;
import fr.miage.premiereimpression.candidature.domain.model.ProfilId;

/**
 * Corps de POST /api/v1/candidatures.
 *
 * @param dateHeureEntretien facultative, au format ISO 8601 (ex. 2026-11-05T14:30)
 */
record CandidatureRequete(
        @NotNull @Positive Long profilId,
        @NotBlank @Size(max = 120) String entreprise,
        @NotBlank @Size(max = 120) String poste,
        @NotBlank @Size(max = 20_000) String texteAnnonce,
        LocalDateTime dateHeureEntretien,
        @Size(max = 255) String adresseEntretien) {

    CreerCandidatureCommande versCommande() {
        return new CreerCandidatureCommande(new ProfilId(profilId), entreprise, poste, texteAnnonce,
                dateHeureEntretien, adresseEntretien);
    }
}
