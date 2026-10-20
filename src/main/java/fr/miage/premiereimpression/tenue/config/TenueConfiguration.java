package fr.miage.premiereimpression.tenue.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.miage.premiereimpression.tenue.application.port.in.GestionMensurationsUseCase;
import fr.miage.premiereimpression.tenue.application.port.in.GestionVetementsUseCase;
import fr.miage.premiereimpression.tenue.application.port.in.RechercheVetementsUseCase;
import fr.miage.premiereimpression.tenue.application.port.out.MensurationsRepository;
import fr.miage.premiereimpression.tenue.application.port.out.ProfilsConnus;
import fr.miage.premiereimpression.tenue.application.port.out.VetementRepository;
import fr.miage.premiereimpression.tenue.application.service.GestionMensurationsService;
import fr.miage.premiereimpression.tenue.application.service.GestionVetementsService;
import fr.miage.premiereimpression.tenue.application.service.RechercheVetementsService;

/**
 * Assemblage de l'hexagone : le domaine et l'application ignorent Spring,
 * c'est ici que les services sont branchés sur les adaptateurs.
 */
@Configuration
class TenueConfiguration {

    @Bean
    GestionMensurationsUseCase gestionMensurationsUseCase(MensurationsRepository mensurations, ProfilsConnus profils) {
        return new GestionMensurationsService(mensurations, profils);
    }

    @Bean
    GestionVetementsUseCase gestionVetementsUseCase(VetementRepository vetements, ProfilsConnus profils) {
        return new GestionVetementsService(vetements, profils);
    }

    @Bean
    RechercheVetementsUseCase rechercheVetementsUseCase(MensurationsRepository mensurations,
            VetementRepository vetements, ProfilsConnus profils) {
        return new RechercheVetementsService(mensurations, vetements, profils);
    }
}
