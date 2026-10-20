package fr.miage.premiereimpression.preparation.application.port.out;

import java.util.List;

import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequis;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequisId;
import fr.miage.premiereimpression.preparation.domain.model.NouveauConceptRequis;

/** Port de sortie : persistance des concepts requis. */
public interface ConceptRequisRepository {

    ConceptRequis ajouter(NouveauConceptRequis concept);

    /** Par rang croissant. */
    List<ConceptRequis> deCandidature(CandidatureId candidature);

    boolean nomDejaSaisi(CandidatureId candidature, String nom);

    /** 0 si la candidature n'a encore aucun concept. */
    int rangMax(CandidatureId candidature);

    /** @return faux si le concept n'existe pas pour cette candidature */
    boolean supprimer(CandidatureId candidature, ConceptRequisId concept);
}
