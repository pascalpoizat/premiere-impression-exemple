package fr.miage.premiereimpression.candidature.domain.algo;

/**
 * Mot retenu après analyse d'un texte.
 *
 * @param racine forme normalisée et racinisée, utilisée pour comparer les textes
 * @param forme  mot tel qu'il apparaît dans le texte (en minuscules), utilisé pour l'affichage
 */
public record Terme(String racine, String forme) {
}
