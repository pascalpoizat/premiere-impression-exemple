package fr.miage.premiereimpression.tenue.domain.model;

/**
 * Évaluation d'un critère.
 *
 * @param ecart vêtement − corps, en cm
 */
public record DetailMesure(Mesure mesure, double corps, double vetement, double ecart, Verdict verdict,
        double penalite) {
}
