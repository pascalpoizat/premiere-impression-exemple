package fr.miage.premiereimpression.candidature.domain.algo;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Pondération TF-IDF des termes d'un document relativement à un corpus.
 * <ul>
 * <li>tf(t, d) = occurrences de t dans d / nombre de termes de d ;</li>
 * <li>idf(t) = 1 + ln((1 + N) / (1 + df(t))), variante lissée (celle de scikit-learn) :
 * jamais nulle ni infinie, minimale (= 1) pour un terme présent dans tous les documents.</li>
 * </ul>
 */
public final class CorpusTfIdf {

    private final int nombreDocuments;
    private final Map<String, Integer> frequencesDocumentaires = new HashMap<>();

    public CorpusTfIdf(Collection<List<Terme>> documents) {
        this.nombreDocuments = documents.size();
        for (List<Terme> document : documents) {
            Set<String> racinesDistinctes = new HashSet<>();
            document.forEach(terme -> racinesDistinctes.add(terme.racine()));
            racinesDistinctes.forEach(racine -> frequencesDocumentaires.merge(racine, 1, Integer::sum));
        }
    }

    public double idf(String racine) {
        int df = frequencesDocumentaires.getOrDefault(racine, 0);
        return 1 + Math.log((1.0 + nombreDocuments) / (1.0 + df));
    }

    /** @return poids TF-IDF de chaque racine du document (vide si le document est vide) */
    public Map<String, Double> vectoriser(List<Terme> document) {
        Map<String, Integer> occurrences = new HashMap<>();
        document.forEach(terme -> occurrences.merge(terme.racine(), 1, Integer::sum));
        Map<String, Double> poids = new HashMap<>();
        double taille = document.size();
        occurrences.forEach((racine, nombre) -> poids.put(racine, nombre / taille * idf(racine)));
        return poids;
    }
}
