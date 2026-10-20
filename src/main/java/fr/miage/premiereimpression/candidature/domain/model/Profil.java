package fr.miage.premiereimpression.candidature.domain.model;

import java.util.Objects;

public record Profil(ProfilId id, String pseudo, String competences) {

    public Profil {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(pseudo, "pseudo");
        Objects.requireNonNull(competences, "competences");
    }
}
