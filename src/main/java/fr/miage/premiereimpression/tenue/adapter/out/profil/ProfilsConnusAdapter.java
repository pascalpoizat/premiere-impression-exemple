package fr.miage.premiereimpression.tenue.adapter.out.profil;

import org.springframework.stereotype.Component;

import fr.miage.premiereimpression.candidature.application.port.in.GestionProfilUseCase;
import fr.miage.premiereimpression.tenue.application.port.out.ProfilsConnus;
import fr.miage.premiereimpression.tenue.domain.model.ProfilId;

/**
 * Adaptateur entre modules : le module tenue interroge le module candidature
 * par son port d'entrée, sans jamais dépendre de son domaine ni de sa base.
 */
@Component
class ProfilsConnusAdapter implements ProfilsConnus {

    private final GestionProfilUseCase profils;

    ProfilsConnusAdapter(GestionProfilUseCase profils) {
        this.profils = profils;
    }

    @Override
    public boolean existe(ProfilId profil) {
        return profils.existe(new fr.miage.premiereimpression.candidature.domain.model.ProfilId(profil.valeur()));
    }
}
