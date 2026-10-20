package fr.miage.premiereimpression.candidature.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import fr.miage.premiereimpression.candidature.application.port.out.CandidatureRepository;
import fr.miage.premiereimpression.candidature.domain.model.Candidature;
import fr.miage.premiereimpression.candidature.domain.model.CandidatureId;
import fr.miage.premiereimpression.candidature.domain.model.NouvelleCandidature;
import fr.miage.premiereimpression.candidature.domain.model.ProfilId;

/** Adaptateur : implémente le port {@link CandidatureRepository} avec Spring Data JPA. */
@Component
class CandidaturePersistenceAdapter implements CandidatureRepository {

    private final CandidatureJpaRepository jpa;

    CandidaturePersistenceAdapter(CandidatureJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Candidature ajouter(NouvelleCandidature candidature) {
        CandidatureJpaEntity entite = new CandidatureJpaEntity(candidature.profil().valeur(),
                candidature.entreprise(), candidature.poste(), candidature.texteAnnonce(),
                candidature.dateHeureEntretien(), candidature.adresseEntretien());
        return versDomaine(jpa.save(entite));
    }

    @Override
    public Optional<Candidature> trouver(CandidatureId id) {
        return jpa.findById(id.valeur()).map(CandidaturePersistenceAdapter::versDomaine);
    }

    @Override
    public List<Candidature> tous() {
        return jpa.findAll(Sort.by("id")).stream().map(CandidaturePersistenceAdapter::versDomaine).toList();
    }

    @Override
    public List<Candidature> deProfil(ProfilId profil) {
        return jpa.findByProfilIdOrderById(profil.valeur()).stream()
                .map(CandidaturePersistenceAdapter::versDomaine).toList();
    }

    @Override
    public List<String> textesDesAnnonces() {
        return jpa.findAllTextesAnnonces();
    }

    private static Candidature versDomaine(CandidatureJpaEntity entite) {
        return new Candidature(new CandidatureId(entite.getId()), new ProfilId(entite.getProfilId()),
                entite.getEntreprise(), entite.getPoste(), entite.getTexteAnnonce(),
                entite.getDateHeureEntretien(), entite.getAdresseEntretien());
    }
}
