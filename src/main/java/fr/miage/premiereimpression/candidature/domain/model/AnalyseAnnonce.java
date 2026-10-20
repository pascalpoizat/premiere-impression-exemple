package fr.miage.premiereimpression.candidature.domain.model;

import java.util.List;

/**
 * Résultat de l'analyse d'une annonce au regard d'un profil.
 *
 * @param correspondance         similarité cosinus annonce / profil, dans [0, 1]
 * @param motsCles               mots-clés de poids maximal, par poids décroissant
 * @param competencesATravailler mots-clés absents du profil, dans le même ordre
 */
public record AnalyseAnnonce(double correspondance, List<MotCle> motsCles, List<MotCle> competencesATravailler) {

    public AnalyseAnnonce {
        motsCles = List.copyOf(motsCles);
        competencesATravailler = List.copyOf(competencesATravailler);
    }
}
