package fr.miage.premiereimpression.tenue.domain.model;

import java.util.Locale;

import fr.miage.premiereimpression.tenue.domain.exception.DonneeInvalideException;

/** Mesures du corps, en cm, avec des bornes de plausibilité pour détecter les erreurs de saisie. */
public enum Mesure {
    TOUR_POITRINE(50, 200),
    TOUR_TAILLE(40, 200),
    TOUR_HANCHES(50, 200),
    LONGUEUR_JAMBE(40, 130),
    CARRURE(25, 70);

    /** Borne haute des mesures d'un vêtement (un vêtement peut être bien plus grand que le corps). */
    static final double MESURE_VETEMENT_MAX = 300;

    private final double minimumCorps;
    private final double maximumCorps;

    Mesure(double minimumCorps, double maximumCorps) {
        this.minimumCorps = minimumCorps;
        this.maximumCorps = maximumCorps;
    }

    double verifierCorps(Double valeur) {
        if (valeur == null || !(valeur >= minimumCorps && valeur <= maximumCorps)) {
            throw new DonneeInvalideException(String.format(Locale.FRANCE,
                    "%s doit être compris entre %.0f et %.0f cm (reçu : %s)", this, minimumCorps, maximumCorps, valeur));
        }
        return valeur;
    }

    double verifierVetement(Double valeur) {
        if (valeur == null || !(valeur > 0 && valeur <= MESURE_VETEMENT_MAX)) {
            throw new DonneeInvalideException(this + " du vêtement doit être compris entre 0 et "
                    + (int) MESURE_VETEMENT_MAX + " cm (reçu : " + valeur + ")");
        }
        return valeur;
    }
}
