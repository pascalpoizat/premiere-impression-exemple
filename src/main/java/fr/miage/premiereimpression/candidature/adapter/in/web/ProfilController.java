package fr.miage.premiereimpression.candidature.adapter.in.web;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import fr.miage.premiereimpression.candidature.application.port.in.GestionProfilUseCase;
import fr.miage.premiereimpression.candidature.domain.model.Profil;
import fr.miage.premiereimpression.candidature.domain.model.ProfilId;

@RestController
@RequestMapping("/api/v1/profils")
class ProfilController {

    private final GestionProfilUseCase gestionProfil;

    ProfilController(GestionProfilUseCase gestionProfil) {
        this.gestionProfil = gestionProfil;
    }

    @PostMapping
    ResponseEntity<ProfilReponse> creer(@Valid @RequestBody ProfilRequete requete) {
        Profil profil = gestionProfil.creerProfil(requete.versCommande());
        URI emplacement = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(profil.id().valeur()).toUri();
        return ResponseEntity.created(emplacement).body(ProfilReponse.depuis(profil));
    }

    @GetMapping("/{id}")
    ProfilReponse trouver(@PathVariable long id) {
        return ProfilReponse.depuis(gestionProfil.trouverProfil(new ProfilId(id)));
    }

    @GetMapping
    List<ProfilReponse> lister() {
        return gestionProfil.listerProfils().stream().map(ProfilReponse::depuis).toList();
    }
}
