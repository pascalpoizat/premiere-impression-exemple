package fr.miage.premiereimpression.tenue.adapter.out.persistence;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import fr.miage.premiereimpression.tenue.application.port.out.VetementRepository;
import fr.miage.premiereimpression.tenue.domain.model.NouveauVetement;
import fr.miage.premiereimpression.tenue.domain.model.ProfilId;
import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;
import fr.miage.premiereimpression.tenue.domain.model.Vetement;
import fr.miage.premiereimpression.tenue.domain.model.VetementId;

/** Adaptateur : implémente le port {@link VetementRepository} avec Spring Data JPA. */
@Component
class VetementPersistenceAdapter implements VetementRepository {

    private final VetementJpaRepository jpa;

    VetementPersistenceAdapter(VetementJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Vetement ajouter(NouveauVetement vetement) {
        VetementJpaEntity entite = new VetementJpaEntity(vetement.proprietaire().valeur(), vetement.type(),
                vetement.nom(), vetement.description(), vetement.mesures());
        return versDomaine(jpa.save(entite));
    }

    @Override
    public List<Vetement> tous() {
        return jpa.findAll(Sort.by("id")).stream().map(VetementPersistenceAdapter::versDomaine).toList();
    }

    @Override
    public List<Vetement> deType(TypeVetement type) {
        return jpa.findByTypeOrderById(type).stream().map(VetementPersistenceAdapter::versDomaine).toList();
    }

    private static Vetement versDomaine(VetementJpaEntity entite) {
        return new Vetement(new VetementId(entite.getId()), new ProfilId(entite.getProprietaireId()),
                entite.getType(), entite.getNom(), entite.getDescription(), entite.getMesures());
    }
}
