package fr.miage.premiereimpression.tenue.adapter.out.persistence;

import static fr.miage.premiereimpression.tenue.domain.model.Mesure.CARRURE;
import static fr.miage.premiereimpression.tenue.domain.model.Mesure.LONGUEUR_JAMBE;
import static fr.miage.premiereimpression.tenue.domain.model.Mesure.TOUR_HANCHES;
import static fr.miage.premiereimpression.tenue.domain.model.Mesure.TOUR_POITRINE;
import static fr.miage.premiereimpression.tenue.domain.model.Mesure.TOUR_TAILLE;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import fr.miage.premiereimpression.tenue.application.port.out.MensurationsRepository;
import fr.miage.premiereimpression.tenue.domain.model.Mensurations;
import fr.miage.premiereimpression.tenue.domain.model.ProfilId;

/** Adaptateur : implémente le port {@link MensurationsRepository} avec Spring Data JPA. */
@Component
class MensurationsPersistenceAdapter implements MensurationsRepository {

    private final MensurationsJpaRepository jpa;

    MensurationsPersistenceAdapter(MensurationsJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Mensurations enregistrer(Mensurations mensurations) {
        // la clé est l'identifiant du profil : save() crée ou remplace la ligne
        MensurationsJpaEntity entite = new MensurationsJpaEntity(mensurations.profil().valeur(),
                mensurations.valeur(TOUR_POITRINE), mensurations.valeur(TOUR_TAILLE),
                mensurations.valeur(TOUR_HANCHES), mensurations.valeur(LONGUEUR_JAMBE), mensurations.valeur(CARRURE));
        return versDomaine(jpa.save(entite));
    }

    @Override
    public Optional<Mensurations> trouver(ProfilId profil) {
        return jpa.findById(profil.valeur()).map(MensurationsPersistenceAdapter::versDomaine);
    }

    private static Mensurations versDomaine(MensurationsJpaEntity entite) {
        return new Mensurations(new ProfilId(entite.getProfilId()), Map.of(
                TOUR_POITRINE, entite.getTourPoitrine(),
                TOUR_TAILLE, entite.getTourTaille(),
                TOUR_HANCHES, entite.getTourHanches(),
                LONGUEUR_JAMBE, entite.getLongueurJambe(),
                CARRURE, entite.getCarrure()));
    }
}
