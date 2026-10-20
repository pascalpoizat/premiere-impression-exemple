package fr.miage.premiereimpression.candidature.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.miage.premiereimpression.candidature.application.port.in.AnalyseAnnonceUseCase;
import fr.miage.premiereimpression.candidature.application.port.in.GestionCandidatureUseCase;
import fr.miage.premiereimpression.candidature.application.port.in.GestionProfilUseCase;
import fr.miage.premiereimpression.candidature.application.port.out.CandidatureRepository;
import fr.miage.premiereimpression.candidature.application.port.out.ProfilRepository;
import fr.miage.premiereimpression.candidature.application.service.AnalyseAnnonceService;
import fr.miage.premiereimpression.candidature.application.service.GestionCandidatureService;
import fr.miage.premiereimpression.candidature.application.service.GestionProfilService;

/**
 * Assemblage de l'hexagone : le domaine et l'application ignorent Spring,
 * c'est ici que les services sont branchés sur les adaptateurs.
 */
@Configuration
class CandidatureConfiguration {

    @Bean
    GestionProfilUseCase gestionProfilUseCase(ProfilRepository profils) {
        return new GestionProfilService(profils);
    }

    @Bean
    GestionCandidatureUseCase gestionCandidatureUseCase(CandidatureRepository candidatures, ProfilRepository profils) {
        return new GestionCandidatureService(candidatures, profils);
    }

    @Bean
    AnalyseAnnonceUseCase analyseAnnonceUseCase(CandidatureRepository candidatures, ProfilRepository profils) {
        return new AnalyseAnnonceService(candidatures, profils);
    }
}
