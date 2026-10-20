package fr.miage.premiereimpression.candidature.domain.algo;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.miage.premiereimpression.candidature.domain.exception.DonneeInvalideException;
import fr.miage.premiereimpression.candidature.domain.model.AnalyseAnnonce;
import fr.miage.premiereimpression.candidature.domain.model.MotCle;

/**
 * Analyse une annonce au regard d'un profil :
 * mots-clés TF-IDF, similarité cosinus, mots-clés absents du profil.
 */
public final class AnalyseurAnnonce {

    public static final int K_MAX = 50;

    /** Poids décroissant, puis racine alphabétique : le résultat est déterministe. */
    private static final Comparator<MotCle> PAR_IMPORTANCE =
            Comparator.comparingDouble(MotCle::poids).reversed().thenComparing(MotCle::racine);

    private final AnalyseurTexte analyseurTexte = new AnalyseurTexte();

    /**
     * @param corpusAnnonces textes de toutes les annonces connues (contient normalement l'annonce analysée)
     * @param k              nombre de mots-clés retenus, dans [1, K_MAX]
     */
    public AnalyseAnnonce analyser(String annonce, String competencesProfil, Collection<String> corpusAnnonces, int k) {
        if (k < 1 || k > K_MAX) {
            throw new DonneeInvalideException("Le nombre de mots-clés doit être compris entre 1 et " + K_MAX);
        }
        CorpusTfIdf corpus = new CorpusTfIdf(corpusAnnonces.stream().map(analyseurTexte::analyser).toList());
        List<Terme> termesAnnonce = analyseurTexte.analyser(annonce);
        Map<String, Double> vecteurAnnonce = corpus.vectoriser(termesAnnonce);
        Map<String, Double> vecteurProfil = corpus.vectoriser(analyseurTexte.analyser(competencesProfil));

        Map<String, String> formes = formesLesPlusFrequentes(termesAnnonce);
        List<MotCle> motsCles = vecteurAnnonce.entrySet().stream()
                .map(poids -> new MotCle(poids.getKey(), formes.get(poids.getKey()), poids.getValue()))
                .sorted(PAR_IMPORTANCE)
                .limit(k)
                .toList();
        List<MotCle> aTravailler = motsCles.stream()
                .filter(motCle -> !vecteurProfil.containsKey(motCle.racine()))
                .toList();
        return new AnalyseAnnonce(SimilariteCosinus.entre(vecteurAnnonce, vecteurProfil), motsCles, aTravailler);
    }

    /** Pour chaque racine, la forme la plus fréquente dans le texte (la plus petite alphabétiquement en cas d'égalité). */
    private static Map<String, String> formesLesPlusFrequentes(List<Terme> termes) {
        Map<String, Map<String, Integer>> comptes = new HashMap<>();
        for (Terme terme : termes) {
            comptes.computeIfAbsent(terme.racine(), racine -> new HashMap<>()).merge(terme.forme(), 1, Integer::sum);
        }
        Map<String, String> formes = new HashMap<>();
        comptes.forEach((racine, parForme) -> formes.put(racine, parForme.entrySet().stream()
                .min(Map.Entry.<String, Integer>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                .orElseThrow()
                .getKey()));
        return formes;
    }
}
