package fr.miage.premiereimpression.tenue.domain.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/** Mensurations complètes d'un profil : toutes les mesures sont obligatoires et plausibles. */
public record Mensurations(ProfilId profil, Map<Mesure, Double> valeurs) {

    public Mensurations {
        Objects.requireNonNull(profil, "profil");
        Map<Mesure, Double> verifiees = new EnumMap<>(Mesure.class);
        for (Mesure mesure : Mesure.values()) {
            verifiees.put(mesure, mesure.verifierCorps(valeurs.get(mesure)));
        }
        valeurs = Collections.unmodifiableMap(verifiees);
    }

    public double valeur(Mesure mesure) {
        return valeurs.get(mesure);
    }
}
