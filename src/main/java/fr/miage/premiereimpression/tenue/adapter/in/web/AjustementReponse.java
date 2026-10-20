package fr.miage.premiereimpression.tenue.adapter.in.web;

import java.util.List;

import fr.miage.premiereimpression.tenue.domain.model.Ajustement;
import fr.miage.premiereimpression.tenue.domain.model.DetailMesure;

record AjustementReponse(VetementReponse vetement, double score, List<DetailMesure> details) {

    static AjustementReponse depuis(Ajustement ajustement) {
        return new AjustementReponse(VetementReponse.depuis(ajustement.vetement()), ajustement.score(),
                ajustement.details());
    }
}
