package fr.miage.premiereimpression.candidature.application.port.in;

import java.util.List;

import fr.miage.premiereimpression.candidature.domain.model.Profil;
import fr.miage.premiereimpression.candidature.domain.model.ProfilId;

/** Port d'entrée : profils des candidats (modèle commun, utilisé aussi par les autres modules). */
public interface GestionProfilUseCase {

    /** @throws fr.miage.premiereimpression.candidature.domain.exception.PseudoDejaPrisException si le pseudo est déjà utilisé */
    Profil creerProfil(CreerProfilCommande commande);

    Profil trouverProfil(ProfilId id);

    boolean existe(ProfilId id);

    List<Profil> listerProfils();
}
