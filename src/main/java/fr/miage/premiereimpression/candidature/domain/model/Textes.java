package fr.miage.premiereimpression.candidature.domain.model;

import fr.miage.premiereimpression.candidature.domain.exception.DonneeInvalideException;

/** Validation commune des champs texte saisis par les utilisateurs. */
final class Textes {

    private Textes() {
    }

    static String obligatoire(String valeur, String champ, int longueurMax) {
        if (valeur == null || valeur.isBlank()) {
            throw new DonneeInvalideException("Le champ « " + champ + " » est obligatoire");
        }
        return facultatif(valeur, champ, longueurMax);
    }

    /** @return le texte sans espaces autour, ou {@code null} s'il est absent ou vide */
    static String facultatif(String valeur, String champ, int longueurMax) {
        if (valeur == null || valeur.isBlank()) {
            return null;
        }
        String nettoye = valeur.strip();
        if (nettoye.length() > longueurMax) {
            throw new DonneeInvalideException("Le champ « " + champ + " » dépasse " + longueurMax + " caractères");
        }
        return nettoye;
    }
}
