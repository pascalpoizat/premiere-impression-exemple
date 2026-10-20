package fr.miage.premiereimpression.preparation.domain.model;

import fr.miage.premiereimpression.preparation.domain.exception.DonneeInvalideException;

/** Échelle des niveaux, fixe pour tous les concepts (un référentiel par concept a été écarté, voir la fiche F3). */
final class Niveaux {

    static final int MIN = 0;
    static final int MAX = 5;
    static final int LONGUEUR_MAX_NOM = 120;

    private Niveaux() {
    }

    static int verifierNiveau(String quoi, int niveau) {
        if (niveau < MIN || niveau > MAX) {
            throw new DonneeInvalideException("Le niveau " + quoi + " doit être entre " + MIN + " et " + MAX + " : " + niveau);
        }
        return niveau;
    }

    static int verifierQtn(int qtn) {
        if (qtn <= 0) {
            throw new DonneeInvalideException("Le coût d'un niveau doit être strictement positif : " + qtn);
        }
        return qtn;
    }

    static String verifierNom(String nom) {
        if (nom == null || nom.isBlank()) {
            throw new DonneeInvalideException("Le nom du concept est obligatoire");
        }
        if (nom.strip().length() > LONGUEUR_MAX_NOM) {
            throw new DonneeInvalideException("Le nom du concept dépasse " + LONGUEUR_MAX_NOM + " caractères");
        }
        return nom.strip();
    }

    static int verifierRang(int rang) {
        if (rang < 1) {
            throw new DonneeInvalideException("Le rang doit être au moins 1 : " + rang);
        }
        return rang;
    }
}
