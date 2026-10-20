package fr.miage.premiereimpression.tenue.adapter.in.web;

import java.util.List;

import fr.miage.premiereimpression.tenue.domain.model.Critere;
import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;

/** Règles d'ajustement d'un type de vêtement, exposées pour que le front sache quelles mesures demander. */
record TypeVetementReponse(TypeVetement type, List<Critere> criteres) {

    static TypeVetementReponse depuis(TypeVetement type) {
        return new TypeVetementReponse(type, type.criteres());
    }
}
