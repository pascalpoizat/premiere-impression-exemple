package fr.miage.premiereimpression.tenue.adapter.in.web;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.miage.premiereimpression.tenue.application.port.in.GestionVetementsUseCase;
import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;

@RestController
@RequestMapping("/api/v1/vetements")
class VetementController {

    private final GestionVetementsUseCase gestionVetements;

    VetementController(GestionVetementsUseCase gestionVetements) {
        this.gestionVetements = gestionVetements;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    VetementReponse declarer(@Valid @RequestBody VetementRequete requete) {
        return VetementReponse.depuis(gestionVetements.declarer(requete.versCommande()));
    }

    @GetMapping
    List<VetementReponse> lister(@RequestParam(required = false) TypeVetement type) {
        return gestionVetements.lister(Optional.ofNullable(type)).stream().map(VetementReponse::depuis).toList();
    }

    @GetMapping("/types")
    List<TypeVetementReponse> types() {
        return Arrays.stream(TypeVetement.values()).map(TypeVetementReponse::depuis).toList();
    }
}
