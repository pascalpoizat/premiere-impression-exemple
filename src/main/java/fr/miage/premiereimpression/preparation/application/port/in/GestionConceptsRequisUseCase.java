package fr.miage.premiereimpression.preparation.application.port.in;

import java.util.List;

import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequis;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequisId;

/** Port d'entrée : saisie des concepts requis d'une candidature. */
public interface GestionConceptsRequisUseCase {

    ConceptRequis ajouter(AjouterConceptRequisCommande commande);

    /** Par rang croissant. */
    List<ConceptRequis> lister(CandidatureId candidature);

    void supprimer(CandidatureId candidature, ConceptRequisId concept);
}
