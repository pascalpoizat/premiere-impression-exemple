package fr.miage.premiereimpression.tenue.adapter.in.web;

import java.util.Map;
import java.util.TreeMap;

import fr.miage.premiereimpression.tenue.domain.model.Mesure;
import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;
import fr.miage.premiereimpression.tenue.domain.model.Vetement;

record VetementReponse(long id, long proprietaireId, TypeVetement type, String nom, String description,
        Map<Mesure, Double> mesures) {

    static VetementReponse depuis(Vetement vetement) {
        return new VetementReponse(vetement.id().valeur(), vetement.proprietaire().valeur(), vetement.type(),
                vetement.nom(), vetement.description(), new TreeMap<>(vetement.mesures()));
    }
}
