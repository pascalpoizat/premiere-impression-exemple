package fr.miage.premiereimpression.candidature.adapter.in.web;

import java.util.List;

import fr.miage.premiereimpression.candidature.domain.model.AnalyseAnnonce;
import fr.miage.premiereimpression.candidature.domain.model.MotCle;

record AnalyseReponse(long candidatureId, double correspondance, List<MotCleReponse> motsCles,
        List<MotCleReponse> competencesATravailler) {

    /** @param terme forme lisible du mot-clé ; @param racine clé de comparaison */
    record MotCleReponse(String terme, String racine, double poids) {

        static MotCleReponse depuis(MotCle motCle) {
            return new MotCleReponse(motCle.forme(), motCle.racine(), motCle.poids());
        }
    }

    static AnalyseReponse depuis(long candidatureId, AnalyseAnnonce analyse) {
        return new AnalyseReponse(candidatureId, analyse.correspondance(),
                analyse.motsCles().stream().map(MotCleReponse::depuis).toList(),
                analyse.competencesATravailler().stream().map(MotCleReponse::depuis).toList());
    }
}
