package fr.miage.premiereimpression.tenue.application.service;

import java.util.List;
import java.util.Optional;

import fr.miage.premiereimpression.tenue.application.port.in.RechercheVetementsUseCase;
import fr.miage.premiereimpression.tenue.application.port.out.MensurationsRepository;
import fr.miage.premiereimpression.tenue.application.port.out.ProfilsConnus;
import fr.miage.premiereimpression.tenue.application.port.out.VetementRepository;
import fr.miage.premiereimpression.tenue.domain.algo.RechercheVetementsCompatibles;
import fr.miage.premiereimpression.tenue.domain.exception.MensurationsIntrouvablesException;
import fr.miage.premiereimpression.tenue.domain.model.Ajustement;
import fr.miage.premiereimpression.tenue.domain.model.Mensurations;
import fr.miage.premiereimpression.tenue.domain.model.ProfilId;
import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;
import fr.miage.premiereimpression.tenue.domain.model.Vetement;

public class RechercheVetementsService implements RechercheVetementsUseCase {

    private final MensurationsRepository mensurations;
    private final VetementRepository vetements;
    private final ProfilsConnus profils;
    private final RechercheVetementsCompatibles recherche = new RechercheVetementsCompatibles();

    public RechercheVetementsService(MensurationsRepository mensurations, VetementRepository vetements,
            ProfilsConnus profils) {
        this.mensurations = mensurations;
        this.vetements = vetements;
        this.profils = profils;
    }

    @Override
    public List<Ajustement> compatibles(ProfilId profil, int k, Optional<TypeVetement> type) {
        VerificationProfil.exiger(profils, profil);
        Mensurations corps = mensurations.trouver(profil)
                .orElseThrow(() -> new MensurationsIntrouvablesException(profil));
        List<Vetement> candidats = type.map(vetements::deType).orElseGet(vetements::tous);
        return recherche.rechercher(corps, candidats, k, type);
    }
}
