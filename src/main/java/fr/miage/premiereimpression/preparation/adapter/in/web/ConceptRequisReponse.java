package fr.miage.premiereimpression.preparation.adapter.in.web;

import fr.miage.premiereimpression.preparation.domain.model.ConceptRequis;

record ConceptRequisReponse(long id, long candidatureId, String nom, int actuel, int requis, int qtn, int rang) {

    static ConceptRequisReponse depuis(ConceptRequis concept) {
        return new ConceptRequisReponse(concept.id().valeur(), concept.candidature().valeur(), concept.nom(),
                concept.actuel(), concept.requis(), concept.qtn(), concept.rang());
    }
}
