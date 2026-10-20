package fr.miage.premiereimpression.tenue.domain.model;

/**
 * Règle d'ajustement d'une mesure pour un type de vêtement.
 *
 * @param aisanceIdeale   écart idéal vêtement − corps, en cm
 * @param toleranceSerree combien on accepte de descendre sous l'aisance idéale avant d'éliminer le vêtement
 * @param echelleAmpleur  écart au-delà de l'aisance idéale qui coûte une pénalité de 1
 */
public record Critere(Mesure mesure, double poids, double aisanceIdeale, double toleranceSerree,
        double echelleAmpleur) {
}
