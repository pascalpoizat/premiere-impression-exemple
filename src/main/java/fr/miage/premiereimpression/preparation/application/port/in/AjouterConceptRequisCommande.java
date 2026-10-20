package fr.miage.premiereimpression.preparation.application.port.in;

import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;

/** Le rang n'est pas fourni : il suit l'ordre de saisie, qui doit refléter l'ordre d'importance dans l'annonce. */
public record AjouterConceptRequisCommande(CandidatureId candidature, String nom, int actuel, int requis, int qtn) {
}
