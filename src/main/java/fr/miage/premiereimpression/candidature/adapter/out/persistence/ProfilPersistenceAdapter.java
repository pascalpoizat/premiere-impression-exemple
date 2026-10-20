package fr.miage.premiereimpression.candidature.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import fr.miage.premiereimpression.candidature.application.port.out.ProfilRepository;
import fr.miage.premiereimpression.candidature.domain.model.NouveauProfil;
import fr.miage.premiereimpression.candidature.domain.model.Profil;
import fr.miage.premiereimpression.candidature.domain.model.ProfilId;

/** Adaptateur : implémente le port {@link ProfilRepository} avec Spring Data JPA. */
@Component
class ProfilPersistenceAdapter implements ProfilRepository {

    private final ProfilJpaRepository jpa;

    ProfilPersistenceAdapter(ProfilJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Profil ajouter(NouveauProfil profil) {
        return versDomaine(jpa.save(new ProfilJpaEntity(profil.pseudo(), profil.competences())));
    }

    @Override
    public Optional<Profil> trouver(ProfilId id) {
        return jpa.findById(id.valeur()).map(ProfilPersistenceAdapter::versDomaine);
    }

    @Override
    public boolean existePseudo(String pseudo) {
        return jpa.existsByPseudo(pseudo);
    }

    @Override
    public List<Profil> tous() {
        return jpa.findAll(Sort.by("id")).stream().map(ProfilPersistenceAdapter::versDomaine).toList();
    }

    private static Profil versDomaine(ProfilJpaEntity entite) {
        return new Profil(new ProfilId(entite.getId()), entite.getPseudo(), entite.getCompetences());
    }
}
