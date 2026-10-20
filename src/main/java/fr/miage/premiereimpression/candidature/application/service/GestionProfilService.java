package fr.miage.premiereimpression.candidature.application.service;

import java.util.List;

import fr.miage.premiereimpression.candidature.application.port.in.CreerProfilCommande;
import fr.miage.premiereimpression.candidature.application.port.in.GestionProfilUseCase;
import fr.miage.premiereimpression.candidature.application.port.out.ProfilRepository;
import fr.miage.premiereimpression.candidature.domain.exception.ProfilIntrouvableException;
import fr.miage.premiereimpression.candidature.domain.exception.PseudoDejaPrisException;
import fr.miage.premiereimpression.candidature.domain.model.NouveauProfil;
import fr.miage.premiereimpression.candidature.domain.model.Profil;
import fr.miage.premiereimpression.candidature.domain.model.ProfilId;

public class GestionProfilService implements GestionProfilUseCase {

    private final ProfilRepository profils;

    public GestionProfilService(ProfilRepository profils) {
        this.profils = profils;
    }

    @Override
    public Profil creerProfil(CreerProfilCommande commande) {
        NouveauProfil profil = new NouveauProfil(commande.pseudo(), commande.competences());
        if (profils.existePseudo(profil.pseudo())) {
            throw new PseudoDejaPrisException(profil.pseudo());
        }
        return profils.ajouter(profil);
    }

    @Override
    public Profil trouverProfil(ProfilId id) {
        return profils.trouver(id).orElseThrow(() -> new ProfilIntrouvableException(id));
    }

    @Override
    public boolean existe(ProfilId id) {
        return profils.trouver(id).isPresent();
    }

    @Override
    public List<Profil> listerProfils() {
        return profils.tous();
    }
}
