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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import fr.miage.premiereimpression.candidature.application.port.in.AnalyseAnnonceUseCase;
import fr.miage.premiereimpression.candidature.application.port.in.GestionCandidatureUseCase;
import fr.miage.premiereimpression.candidature.domain.model.Candidature;
import fr.miage.premiereimpression.candidature.domain.model.CandidatureId;
import fr.miage.premiereimpression.candidature.domain.model.ProfilId;

@RestController
@RequestMapping("/api/v1/candidatures")
class CandidatureController {

    private final GestionCandidatureUseCase gestionCandidature;
    private final AnalyseAnnonceUseCase analyseAnnonce;

    CandidatureController(GestionCandidatureUseCase gestionCandidature, AnalyseAnnonceUseCase analyseAnnonce) {
        this.gestionCandidature = gestionCandidature;
        this.analyseAnnonce = analyseAnnonce;
    }

    @PostMapping
    ResponseEntity<CandidatureReponse> creer(@Valid @RequestBody CandidatureRequete requete) {
        Candidature candidature = gestionCandidature.creerCandidature(requete.versCommande());
        URI emplacement = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(candidature.id().valeur()).toUri();
        return ResponseEntity.created(emplacement).body(CandidatureReponse.depuis(candidature));
    }

    @GetMapping("/{id}")
    CandidatureReponse trouver(@PathVariable long id) {
        return CandidatureReponse.depuis(gestionCandidature.trouverCandidature(new CandidatureId(id)));
    }

    /** Toutes les candidatures, ou seulement celles d'un profil : GET /api/v1/candidatures?profilId=1 */
    @GetMapping
    List<CandidatureReponse> lister(@RequestParam(required = false) Long profilId) {
        List<Candidature> candidatures = profilId == null
                ? gestionCandidature.listerCandidatures()
                : gestionCandidature.listerCandidaturesDe(new ProfilId(profilId));
        return candidatures.stream().map(CandidatureReponse::depuis).toList();
    }

    /** Exemple : GET /api/v1/candidatures/3/analyse?k=10 */
    @GetMapping("/{id}/analyse")
    AnalyseReponse analyser(@PathVariable long id, @RequestParam(defaultValue = "10") int k) {
        return AnalyseReponse.depuis(id, analyseAnnonce.analyser(new CandidatureId(id), k));
    }
}
