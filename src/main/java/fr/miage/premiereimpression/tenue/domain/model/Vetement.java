package fr.miage.premiereimpression.tenue.domain.model;

import java.util.Map;
import java.util.Objects;

public record Vetement(VetementId id, ProfilId proprietaire, TypeVetement type, String nom, String description,
        Map<Mesure, Double> mesures) {

    public Vetement {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(proprietaire, "proprietaire");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(nom, "nom");
        mesures = Map.copyOf(mesures);
    }

    public double mesure(Mesure mesure) {
        Double valeur = mesures.get(mesure);
        if (valeur == null) {
            throw new IllegalStateException("Mesure " + mesure + " absente du vêtement " + id.valeur());
        }
        return valeur;
    }
}
