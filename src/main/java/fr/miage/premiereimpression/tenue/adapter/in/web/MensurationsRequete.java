package fr.miage.premiereimpression.tenue.adapter.in.web;

import java.util.Map;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import fr.miage.premiereimpression.tenue.domain.model.Mesure;

/** Corps de PUT /api/v1/profils/{id}/mensurations, en cm. La plausibilité est vérifiée dans le domaine. */
record MensurationsRequete(
        @NotNull @Positive Double tourPoitrine,
        @NotNull @Positive Double tourTaille,
        @NotNull @Positive Double tourHanches,
        @NotNull @Positive Double longueurJambe,
        @NotNull @Positive Double carrure) {

    Map<Mesure, Double> versMesures() {
        return Map.of(Mesure.TOUR_POITRINE, tourPoitrine, Mesure.TOUR_TAILLE, tourTaille,
                Mesure.TOUR_HANCHES, tourHanches, Mesure.LONGUEUR_JAMBE, longueurJambe, Mesure.CARRURE, carrure);
    }
}
