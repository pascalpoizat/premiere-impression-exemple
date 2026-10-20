package fr.miage.premiereimpression.preparation.domain.model;

import java.util.Objects;

import fr.miage.premiereimpression.preparation.domain.exception.DonneeInvalideException;

/** {@code (j, Cᵢ→k, δ)} : {@code duree} minutes passées le jour de rang {@code jour} sur un niveau. */
public record Bloc(int jour, Niveau niveau, int duree) {

    public Bloc {
        Objects.requireNonNull(niveau, "niveau");
        if (jour < 0) {
            throw new DonneeInvalideException("Le rang du jour ne peut pas être négatif : " + jour);
        }
        if (duree <= 0) {
            throw new DonneeInvalideException("La durée d'un bloc doit être strictement positive : " + duree);
        }
    }
}
