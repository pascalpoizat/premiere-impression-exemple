package fr.miage.premiereimpression.tenue.adapter.in.web;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.miage.premiereimpression.tenue.application.port.in.RechercheVetementsUseCase;
import fr.miage.premiereimpression.tenue.domain.model.ProfilId;
import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;

@RestController
class AjustementController {

    private final RechercheVetementsUseCase rechercheVetements;

    AjustementController(RechercheVetementsUseCase rechercheVetements) {
        this.rechercheVetements = rechercheVetements;
    }

    /** Exemple : GET /api/v1/profils/1/vetements-compatibles?k=5&amp;type=VESTE */
    @GetMapping("/api/v1/profils/{profilId}/vetements-compatibles")
    List<AjustementReponse> compatibles(@PathVariable long profilId, @RequestParam(defaultValue = "10") int k,
            @RequestParam(required = false) TypeVetement type) {
        return rechercheVetements.compatibles(new ProfilId(profilId), k, Optional.ofNullable(type)).stream()
                .map(AjustementReponse::depuis)
                .toList();
    }
}
