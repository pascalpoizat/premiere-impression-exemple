package fr.miage.premiereimpression.preparation.domain.model;

import java.util.Objects;

/**
 * Concept requis par une candidature, avec les notations de la fiche F3 :
 * niveau actuel et requis (0 à 5), coût d'un niveau {@code qtn} en minutes, rang (ordre d'importance).
 */
public record ConceptRequis(ConceptRequisId id, CandidatureId candidature, String nom, int actuel, int requis,
        int qtn, int rang) {

    public ConceptRequis {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(candidature, "candidature");
        nom = Niveaux.verifierNom(nom);
        Niveaux.verifierNiveau("actuel", actuel);
        Niveaux.verifierNiveau("requis", requis);
        Niveaux.verifierQtn(qtn);
        Niveaux.verifierRang(rang);
    }

    /** {@code nrᵢ} : niveaux restant à monter pour atteindre le niveau requis. */
    public int nr() {
        return Math.max(0, requis - actuel);
    }

    /** {@code resteᵢ = nrᵢ · qtnᵢ}, en minutes. */
    public long reste() {
        return (long) nr() * qtn;
    }

    /** Le même concept, un niveau plus haut : sert à faire avancer le calcul sans modifier le concept saisi. */
    public ConceptRequis unNiveauPlusHaut() {
        return new ConceptRequis(id, candidature, nom, actuel + 1, requis, qtn, rang);
    }
}
