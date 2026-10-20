package fr.miage.premiereimpression.candidature.application.port.in;

import java.time.LocalDateTime;

import fr.miage.premiereimpression.candidature.domain.model.ProfilId;

public record CreerCandidatureCommande(ProfilId profil, String entreprise, String poste, String texteAnnonce,
        LocalDateTime dateHeureEntretien, String adresseEntretien) {
}
