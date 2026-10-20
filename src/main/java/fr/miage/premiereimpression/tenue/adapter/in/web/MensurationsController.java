package fr.miage.premiereimpression.tenue.adapter.in.web;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.miage.premiereimpression.tenue.application.port.in.GestionMensurationsUseCase;
import fr.miage.premiereimpression.tenue.domain.model.ProfilId;

/** Les mensurations sont une sous-ressource du profil : PUT crée ou remplace. */
@RestController
@RequestMapping("/api/v1/profils/{profilId}/mensurations")
class MensurationsController {

    private final GestionMensurationsUseCase gestionMensurations;

    MensurationsController(GestionMensurationsUseCase gestionMensurations) {
        this.gestionMensurations = gestionMensurations;
    }

    @PutMapping
    MensurationsReponse enregistrer(@PathVariable long profilId, @Valid @RequestBody MensurationsRequete requete) {
        return MensurationsReponse.depuis(gestionMensurations.enregistrer(new ProfilId(profilId), requete.versMesures()));
    }

    @GetMapping
    MensurationsReponse trouver(@PathVariable long profilId) {
        return MensurationsReponse.depuis(gestionMensurations.trouver(new ProfilId(profilId)));
    }
}
