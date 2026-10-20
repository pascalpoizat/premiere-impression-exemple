package fr.miage.premiereimpression.preparation.adapter.in.web;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import fr.miage.premiereimpression.preparation.application.port.in.GestionConceptsRequisUseCase;
import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequis;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequisId;

@RestController
@RequestMapping("/api/v1/candidatures/{candidatureId}/concepts-requis")
class ConceptRequisController {

    private final GestionConceptsRequisUseCase gestionConcepts;

    ConceptRequisController(GestionConceptsRequisUseCase gestionConcepts) {
        this.gestionConcepts = gestionConcepts;
    }

    @PostMapping
    ResponseEntity<ConceptRequisReponse> ajouter(@PathVariable long candidatureId,
            @Valid @RequestBody ConceptRequisRequete requete) {
        ConceptRequis concept = gestionConcepts.ajouter(requete.versCommande(candidatureId));
        URI emplacement = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(concept.id().valeur()).toUri();
        return ResponseEntity.created(emplacement).body(ConceptRequisReponse.depuis(concept));
    }

    /** Par rang croissant. */
    @GetMapping
    List<ConceptRequisReponse> lister(@PathVariable long candidatureId) {
        return gestionConcepts.lister(new CandidatureId(candidatureId)).stream()
                .map(ConceptRequisReponse::depuis).toList();
    }

    @DeleteMapping("/{conceptId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void supprimer(@PathVariable long candidatureId, @PathVariable long conceptId) {
        gestionConcepts.supprimer(new CandidatureId(candidatureId), new ConceptRequisId(conceptId));
    }
}
