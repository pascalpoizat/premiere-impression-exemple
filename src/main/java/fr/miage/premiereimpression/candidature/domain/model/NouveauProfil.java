package fr.miage.premiereimpression.candidature.domain.model;

/** Profil pas encore enregistré : c'est ici que les règles métier sont vérifiées. */
public record NouveauProfil(String pseudo, String competences) {

    static final int LONGUEUR_MAX_PSEUDO = 50;
    static final int LONGUEUR_MAX_COMPETENCES = 5000;

    public NouveauProfil {
        pseudo = Textes.obligatoire(pseudo, "pseudo", LONGUEUR_MAX_PSEUDO);
        String nettoyees = Textes.facultatif(competences, "compétences", LONGUEUR_MAX_COMPETENCES);
        competences = nettoyees == null ? "" : nettoyees;
    }
}
