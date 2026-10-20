package fr.miage.premiereimpression.tenue.application.service;

import java.util.Map;

import fr.miage.premiereimpression.tenue.application.port.in.GestionMensurationsUseCase;
import fr.miage.premiereimpression.tenue.application.port.out.MensurationsRepository;
import fr.miage.premiereimpression.tenue.application.port.out.ProfilsConnus;
import fr.miage.premiereimpression.tenue.domain.exception.MensurationsIntrouvablesException;
import fr.miage.premiereimpression.tenue.domain.model.Mensurations;
import fr.miage.premiereimpression.tenue.domain.model.Mesure;
import fr.miage.premiereimpression.tenue.domain.model.ProfilId;

public class GestionMensurationsService implements GestionMensurationsUseCase {

    private final MensurationsRepository mensurations;
    private final ProfilsConnus profils;

    public GestionMensurationsService(MensurationsRepository mensurations, ProfilsConnus profils) {
        this.mensurations = mensurations;
        this.profils = profils;
    }

    @Override
    public Mensurations enregistrer(ProfilId profil, Map<Mesure, Double> valeurs) {
        Mensurations nouvelles = new Mensurations(profil, valeurs);
        VerificationProfil.exiger(profils, profil);
        return mensurations.enregistrer(nouvelles);
    }

    @Override
    public Mensurations trouver(ProfilId profil) {
        VerificationProfil.exiger(profils, profil);
        return mensurations.trouver(profil).orElseThrow(() -> new MensurationsIntrouvablesException(profil));
    }
}
