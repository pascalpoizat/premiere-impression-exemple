package fr.miage.premiereimpression.candidature.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import fr.miage.premiereimpression.candidature.application.port.in.CreerProfilCommande;

/** Corps de POST /api/v1/profils. La validation de forme est faite ici, les règles métier dans le domaine. */
record ProfilRequete(@NotBlank @Size(max = 50) String pseudo, @Size(max = 5000) String competences) {

    CreerProfilCommande versCommande() {
        return new CreerProfilCommande(pseudo, competences);
    }
}
