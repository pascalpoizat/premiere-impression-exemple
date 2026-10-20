package fr.miage.premiereimpression.tenue.application.port.out;

import fr.miage.premiereimpression.tenue.domain.model.ProfilId;

/** Port de sortie : les profils appartiennent au module candidature, on lui demande s'ils existent. */
public interface ProfilsConnus {

    boolean existe(ProfilId profil);
}
