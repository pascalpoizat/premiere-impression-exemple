package fr.miage.premiereimpression.preparation.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Component;

import fr.miage.premiereimpression.preparation.application.port.out.ConceptRequisRepository;
import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequis;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequisId;
import fr.miage.premiereimpression.preparation.domain.model.NouveauConceptRequis;

/** Adaptateur : implémente le port {@link ConceptRequisRepository} avec Spring Data JPA. */
@Component
class ConceptRequisPersistenceAdapter implements ConceptRequisRepository {

    private final ConceptRequisJpaRepository jpa;

    ConceptRequisPersistenceAdapter(ConceptRequisJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public ConceptRequis ajouter(NouveauConceptRequis concept) {
        ConceptRequisJpaEntity entite = new ConceptRequisJpaEntity(concept.candidature().valeur(), concept.nom(),
                concept.actuel(), concept.requis(), concept.qtn(), concept.rang());
        return versDomaine(jpa.save(entite));
    }

    @Override
    public List<ConceptRequis> deCandidature(CandidatureId candidature) {
        return jpa.findByCandidatureIdOrderByRang(candidature.valeur()).stream()
                .map(ConceptRequisPersistenceAdapter::versDomaine).toList();
    }

    @Override
    public boolean nomDejaSaisi(CandidatureId candidature, String nom) {
        return jpa.existsByCandidatureIdAndNom(candidature.valeur(), nom);
    }

    @Override
    public int rangMax(CandidatureId candidature) {
        return jpa.rangMax(candidature.valeur());
    }

    @Override
    public boolean supprimer(CandidatureId candidature, ConceptRequisId concept) {
        return jpa.deleteByIdAndCandidatureId(concept.valeur(), candidature.valeur()) > 0;
    }

    private static ConceptRequis versDomaine(ConceptRequisJpaEntity entite) {
        return new ConceptRequis(new ConceptRequisId(entite.getId()), new CandidatureId(entite.getCandidatureId()),
                entite.getNom(), entite.getActuel(), entite.getRequis(), entite.getQtn(), entite.getRang());
    }
}
