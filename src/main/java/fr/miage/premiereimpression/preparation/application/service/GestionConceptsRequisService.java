package fr.miage.premiereimpression.preparation.application.service;

import java.util.List;

import fr.miage.premiereimpression.preparation.application.port.in.AjouterConceptRequisCommande;
import fr.miage.premiereimpression.preparation.application.port.in.GestionConceptsRequisUseCase;
import fr.miage.premiereimpression.preparation.application.port.out.CandidaturesConnues;
import fr.miage.premiereimpression.preparation.application.port.out.ConceptRequisRepository;
import fr.miage.premiereimpression.preparation.domain.exception.ConceptDejaSaisiException;
import fr.miage.premiereimpression.preparation.domain.exception.ConceptRequisInconnuException;
import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequis;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequisId;
import fr.miage.premiereimpression.preparation.domain.model.NouveauConceptRequis;

public class GestionConceptsRequisService implements GestionConceptsRequisUseCase {

    private final ConceptRequisRepository concepts;
    private final CandidaturesConnues candidatures;

    public GestionConceptsRequisService(ConceptRequisRepository concepts, CandidaturesConnues candidatures) {
        this.concepts = concepts;
        this.candidatures = candidatures;
    }

    @Override
    public ConceptRequis ajouter(AjouterConceptRequisCommande commande) {
        CandidatureId candidature = commande.candidature();
        VerificationCandidature.exiger(candidatures, candidature);
        int rang = concepts.rangMax(candidature) + 1;
        NouveauConceptRequis concept = new NouveauConceptRequis(candidature, commande.nom(), commande.actuel(),
                commande.requis(), commande.qtn(), rang);
        if (concepts.nomDejaSaisi(candidature, concept.nom())) {
            throw new ConceptDejaSaisiException(candidature, concept.nom());
        }
        return concepts.ajouter(concept);
    }

    @Override
    public List<ConceptRequis> lister(CandidatureId candidature) {
        VerificationCandidature.exiger(candidatures, candidature);
        return concepts.deCandidature(candidature);
    }

    @Override
    public void supprimer(CandidatureId candidature, ConceptRequisId concept) {
        VerificationCandidature.exiger(candidatures, candidature);
        if (!concepts.supprimer(candidature, concept)) {
            throw new ConceptRequisInconnuException(candidature, concept);
        }
    }
}
