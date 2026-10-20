package fr.miage.premiereimpression.preparation.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.miage.premiereimpression.preparation.application.port.in.GestionConceptsRequisUseCase;
import fr.miage.premiereimpression.preparation.application.port.in.PlanningRevisionUseCase;
import fr.miage.premiereimpression.preparation.application.port.out.CandidaturesConnues;
import fr.miage.premiereimpression.preparation.application.port.out.ConceptRequisRepository;
import fr.miage.premiereimpression.preparation.application.service.GestionConceptsRequisService;
import fr.miage.premiereimpression.preparation.application.service.PlanningRevisionService;

/**
 * Assemblage de l'hexagone : le domaine et l'application ignorent Spring,
 * c'est ici que les services sont branchés sur les adaptateurs.
 */
@Configuration
class PreparationConfiguration {

    @Bean
    GestionConceptsRequisUseCase gestionConceptsRequisUseCase(ConceptRequisRepository concepts,
            CandidaturesConnues candidatures) {
        return new GestionConceptsRequisService(concepts, candidatures);
    }

    @Bean
    PlanningRevisionUseCase planningRevisionUseCase(ConceptRequisRepository concepts,
            CandidaturesConnues candidatures) {
        return new PlanningRevisionService(concepts, candidatures, Clock.systemDefaultZone());
    }
}
