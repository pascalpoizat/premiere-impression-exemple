package fr.miage.premiereimpression.candidature.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/** Entité pivot du projet : toutes les features se rattachent à une candidature et à son entretien. */
public record Candidature(CandidatureId id, ProfilId profil, String entreprise, String poste, String texteAnnonce,
        LocalDateTime dateHeureEntretien, String adresseEntretien) {

    public Candidature {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(profil, "profil");
        Objects.requireNonNull(entreprise, "entreprise");
        Objects.requireNonNull(poste, "poste");
        Objects.requireNonNull(texteAnnonce, "texteAnnonce");
    }
}
