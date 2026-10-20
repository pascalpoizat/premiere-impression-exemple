package fr.miage.premiereimpression.candidature.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Candidature pas encore enregistrée.
 *
 * @param dateHeureEntretien facultative : l'entretien n'est pas toujours fixé au moment de la candidature
 * @param adresseEntretien   facultative
 */
public record NouvelleCandidature(ProfilId profil, String entreprise, String poste, String texteAnnonce,
        LocalDateTime dateHeureEntretien, String adresseEntretien) {

    static final int LONGUEUR_MAX_INTITULE = 120;
    static final int LONGUEUR_MAX_ANNONCE = 20_000;
    static final int LONGUEUR_MAX_ADRESSE = 255;

    public NouvelleCandidature {
        Objects.requireNonNull(profil, "profil");
        entreprise = Textes.obligatoire(entreprise, "entreprise", LONGUEUR_MAX_INTITULE);
        poste = Textes.obligatoire(poste, "poste", LONGUEUR_MAX_INTITULE);
        texteAnnonce = Textes.obligatoire(texteAnnonce, "texte de l'annonce", LONGUEUR_MAX_ANNONCE);
        adresseEntretien = Textes.facultatif(adresseEntretien, "adresse de l'entretien", LONGUEUR_MAX_ADRESSE);
    }
}
