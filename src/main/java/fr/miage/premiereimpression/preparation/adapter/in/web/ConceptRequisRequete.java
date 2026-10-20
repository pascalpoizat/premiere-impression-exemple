package fr.miage.premiereimpression.preparation.adapter.in.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import fr.miage.premiereimpression.preparation.application.port.in.AjouterConceptRequisCommande;
import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;

/**
 * Corps de POST /api/v1/candidatures/{id}/concepts-requis. Le rang n'est pas fourni : il suit l'ordre de saisie.
 *
 * @param qtn coût d'un niveau, en minutes
 */
record ConceptRequisRequete(
        @NotBlank @Size(max = 120) String nom,
        @NotNull @Min(0) @Max(5) Integer actuel,
        @NotNull @Min(0) @Max(5) Integer requis,
        @NotNull @Positive Integer qtn) {

    AjouterConceptRequisCommande versCommande(long candidatureId) {
        return new AjouterConceptRequisCommande(new CandidatureId(candidatureId), nom, actuel, requis, qtn);
    }
}
