package fr.miage.premiereimpression.candidature.application.service;

import java.util.List;

import fr.miage.premiereimpression.candidature.application.port.in.CreerCandidatureCommande;
import fr.miage.premiereimpression.candidature.application.port.in.GestionCandidatureUseCase;
import fr.miage.premiereimpression.candidature.application.port.out.CandidatureRepository;
import fr.miage.premiereimpression.candidature.application.port.out.ProfilRepository;
import fr.miage.premiereimpression.candidature.domain.exception.CandidatureIntrouvableException;
import fr.miage.premiereimpression.candidature.domain.exception.ProfilIntrouvableException;
import fr.miage.premiereimpression.candidature.domain.model.Candidature;
import fr.miage.premiereimpression.candidature.domain.model.CandidatureId;
import fr.miage.premiereimpression.candidature.domain.model.NouvelleCandidature;
import fr.miage.premiereimpression.candidature.domain.model.ProfilId;

public class GestionCandidatureService implements GestionCandidatureUseCase {

    private final CandidatureRepository candidatures;
    private final ProfilRepository profils;

    public GestionCandidatureService(CandidatureRepository candidatures, ProfilRepository profils) {
        this.candidatures = candidatures;
        this.profils = profils;
    }

    @Override
    public Candidature creerCandidature(CreerCandidatureCommande commande) {
        NouvelleCandidature candidature = new NouvelleCandidature(commande.profil(), commande.entreprise(),
                commande.poste(), commande.texteAnnonce(), commande.dateHeureEntretien(), commande.adresseEntretien());
        verifierProfilExiste(candidature.profil());
        return candidatures.ajouter(candidature);
    }

    private void verifierProfilExiste(ProfilId profil) {
        if (profils.trouver(profil).isEmpty()) {
            throw new ProfilIntrouvableException(profil);
        }
    }

    @Override
    public Candidature trouverCandidature(CandidatureId id) {
        return candidatures.trouver(id).orElseThrow(() -> new CandidatureIntrouvableException(id));
    }

    @Override
    public List<Candidature> listerCandidatures() {
        return candidatures.tous();
    }

    @Override
    public List<Candidature> listerCandidaturesDe(ProfilId profil) {
        verifierProfilExiste(profil);
        return candidatures.deProfil(profil);
    }
}
