package fr.miage.premiereimpression.tenue.domain.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import fr.miage.premiereimpression.tenue.domain.exception.DonneeInvalideException;

/** Vêtement pas encore enregistré : il doit fournir exactement les mesures utiles à son type. */
public record NouveauVetement(ProfilId proprietaire, TypeVetement type, String nom, String description,
        Map<Mesure, Double> mesures) {

    static final int LONGUEUR_MAX_NOM = 120;
    static final int LONGUEUR_MAX_DESCRIPTION = 1000;

    public NouveauVetement {
        Objects.requireNonNull(proprietaire, "proprietaire");
        Objects.requireNonNull(type, "type");
        nom = verifierNom(nom);
        description = verifierDescription(description);
        mesures = verifierMesures(type, mesures);
    }

    private static String verifierNom(String nom) {
        if (nom == null || nom.isBlank()) {
            throw new DonneeInvalideException("Le nom du vêtement est obligatoire");
        }
        if (nom.strip().length() > LONGUEUR_MAX_NOM) {
            throw new DonneeInvalideException("Le nom du vêtement dépasse " + LONGUEUR_MAX_NOM + " caractères");
        }
        return nom.strip();
    }

    private static String verifierDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        if (description.strip().length() > LONGUEUR_MAX_DESCRIPTION) {
            throw new DonneeInvalideException("La description dépasse " + LONGUEUR_MAX_DESCRIPTION + " caractères");
        }
        return description.strip();
    }

    private static Map<Mesure, Double> verifierMesures(TypeVetement type, Map<Mesure, Double> mesures) {
        if (mesures == null || !mesures.keySet().equals(type.mesuresRequises())) {
            throw new DonneeInvalideException("Un vêtement de type " + type + " doit avoir exactement les mesures "
                    + type.mesuresRequises());
        }
        Map<Mesure, Double> verifiees = new EnumMap<>(Mesure.class);
        mesures.forEach((mesure, valeur) -> verifiees.put(mesure, mesure.verifierVetement(valeur)));
        return Collections.unmodifiableMap(verifiees);
    }
}
