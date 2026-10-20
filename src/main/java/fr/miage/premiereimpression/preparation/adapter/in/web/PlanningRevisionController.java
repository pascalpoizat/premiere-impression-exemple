package fr.miage.premiereimpression.preparation.adapter.in.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.miage.premiereimpression.preparation.application.port.in.PlanningRevisionUseCase;
import fr.miage.premiereimpression.preparation.domain.algo.PlacementStrategyType;
import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;

@RestController
class PlanningRevisionController {

    private final PlanningRevisionUseCase planningRevision;

    PlanningRevisionController(PlanningRevisionUseCase planningRevision) {
        this.planningRevision = planningRevision;
    }

    /**
     * Exemple : GET /api/v1/candidatures/3/planning-revision?strategie=CLOSE_TO_ACHIEVEMENT&amp;disponibilites=120,90,0
     * (une durée en minutes par jour, du jour même à la veille de l'entretien).
     */
    @GetMapping("/api/v1/candidatures/{candidatureId}/planning-revision")
    PlanningReponse planifier(@PathVariable long candidatureId, @RequestParam PlacementStrategyType strategie,
            @RequestParam(defaultValue = "") List<Integer> disponibilites) {
        return PlanningReponse.depuis(candidatureId, strategie,
                planningRevision.planifier(new CandidatureId(candidatureId), strategie, disponibilites));
    }
}
