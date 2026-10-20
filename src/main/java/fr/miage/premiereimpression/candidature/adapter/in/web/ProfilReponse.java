package fr.miage.premiereimpression.candidature.adapter.in.web;

import fr.miage.premiereimpression.candidature.domain.model.Profil;

record ProfilReponse(long id, String pseudo, String competences) {

    static ProfilReponse depuis(Profil profil) {
        return new ProfilReponse(profil.id().valeur(), profil.pseudo(), profil.competences());
    }
}
