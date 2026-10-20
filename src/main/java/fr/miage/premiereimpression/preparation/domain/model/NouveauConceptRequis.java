package fr.miage.premiereimpression.preparation.domain.model;

import java.util.Objects;

/**
 * Concept requis pas encore enregistré. Il peut être déjà au niveau requis (actuel ≥ requis) :
 * il est alors accepté, et simplement ignoré par le planning.
 */
public record NouveauConceptRequis(CandidatureId candidature, String nom, int actuel, int requis, int qtn, int rang) {

    public NouveauConceptRequis {
        Objects.requireNonNull(candidature, "candidature");
        nom = Niveaux.verifierNom(nom);
        Niveaux.verifierNiveau("actuel", actuel);
        Niveaux.verifierNiveau("requis", requis);
        Niveaux.verifierQtn(qtn);
        Niveaux.verifierRang(rang);
    }
}
