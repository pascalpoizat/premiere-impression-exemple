package fr.miage.premiereimpression.candidature.domain.algo;

import java.util.Comparator;
import java.util.List;

/**
 * Racinisation simplifiée du français, inspirée de l'algorithme de Porter mais réduite à un seul passage :
 * <ol>
 * <li>suppression de la marque du pluriel ({@code s} ou {@code x} final, sauf {@code ss}) ;</li>
 * <li>suppression du suffixe le plus long de la liste, s'il laisse une racine assez longue.</li>
 * </ol>
 * Exemple : développement, développeur, développeurs, développer, développerez donnent tous {@code developp}.
 * Les suffixes {@code erez} et {@code rez} traitent le futur de la 2e personne, très fréquent dans les annonces.
 * La racine n'est pas forcément un vrai mot : elle sert seulement à comparer des textes entre eux.
 */
final class Raciniseur {

    private static final int RACINE_MIN = 3;
    /** Le suffixe « er » est ambigu en anglais (docker, manager) : on exige une racine plus longue. */
    private static final int RACINE_MIN_SUFFIXE_ER = 5;

    private static final List<String> SUFFIXES = List.of(
            "issement", "ement", "ment", "ation", "ateur", "atrice", "ance", "ence", "ite", "ive", "if",
            "euse", "eur", "eux", "able", "ible", "ique", "iste", "isme", "erez", "rez", "er", "e")
            .stream()
            .sorted(Comparator.comparingInt(String::length).reversed())
            .toList();

    private Raciniseur() {
    }

    /** @param mot mot normalisé : minuscules, sans accents, uniquement des lettres */
    static String raciner(String mot) {
        String singulier = retirerPluriel(mot);
        for (String suffixe : SUFFIXES) {
            int longueurRacine = singulier.length() - suffixe.length();
            if (singulier.endsWith(suffixe) && longueurRacine >= longueurMinimale(suffixe)) {
                return singulier.substring(0, longueurRacine);
            }
        }
        return singulier;
    }

    private static String retirerPluriel(String mot) {
        boolean pluriel = (mot.endsWith("s") && !mot.endsWith("ss")) || mot.endsWith("x");
        return pluriel && mot.length() > RACINE_MIN ? mot.substring(0, mot.length() - 1) : mot;
    }

    private static int longueurMinimale(String suffixe) {
        return "er".equals(suffixe) ? RACINE_MIN_SUFFIXE_ER : RACINE_MIN;
    }
}
