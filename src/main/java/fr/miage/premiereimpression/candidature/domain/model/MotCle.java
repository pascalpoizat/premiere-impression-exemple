package fr.miage.premiereimpression.candidature.domain.model;

/**
 * Terme pondéré d'une annonce.
 *
 * @param racine racine produite par la racinisation (clé de comparaison)
 * @param forme  forme la plus fréquente dans l'annonce (pour l'affichage)
 * @param poids  poids TF-IDF
 */
public record MotCle(String racine, String forme, double poids) {
}
