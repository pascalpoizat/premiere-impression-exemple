package fr.miage.premiereimpression.tenue.adapter.in.web;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import fr.miage.premiereimpression.tenue.application.port.in.DeclarerVetementCommande;
import fr.miage.premiereimpression.tenue.domain.model.Mesure;
import fr.miage.premiereimpression.tenue.domain.model.ProfilId;
import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;

/**
 * Corps de POST /api/v1/vetements.
 *
 * @param mesures mesures à plat du vêtement en cm, exactement celles de son type (voir GET /api/v1/vetements/types)
 */
record VetementRequete(
        @NotNull @Positive Long proprietaireId,
        @NotNull TypeVetement type,
        @NotBlank @Size(max = 120) String nom,
        @Size(max = 1000) String description,
        @NotEmpty Map<Mesure, Double> mesures) {

    DeclarerVetementCommande versCommande() {
        return new DeclarerVetementCommande(new ProfilId(proprietaireId), type, nom, description, mesures);
    }
}
