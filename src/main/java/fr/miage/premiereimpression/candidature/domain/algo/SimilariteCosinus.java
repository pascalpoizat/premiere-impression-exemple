package fr.miage.premiereimpression.candidature.domain.algo;

import java.util.Map;

/**
 * Similarité cosinus entre deux vecteurs creux : (a · b) / (‖a‖ × ‖b‖).
 * Avec des poids positifs, le résultat est dans [0, 1] ; il vaut 0 si l'un des vecteurs est nul.
 */
public final class SimilariteCosinus {

    private SimilariteCosinus() {
    }

    public static double entre(Map<String, Double> a, Map<String, Double> b) {
        double normes = norme(a) * norme(b);
        if (normes == 0) {
            return 0;
        }
        // on parcourt le plus petit vecteur : O(min(|a|, |b|))
        Map<String, Double> petit = a.size() <= b.size() ? a : b;
        Map<String, Double> grand = petit == a ? b : a;
        double produitScalaire = 0;
        for (Map.Entry<String, Double> composante : petit.entrySet()) {
            produitScalaire += composante.getValue() * grand.getOrDefault(composante.getKey(), 0.0);
        }
        // borne contre les erreurs d'arrondi (cos(x, x) peut valoir 1.0000000000000002)
        return Math.min(1.0, produitScalaire / normes);
    }

    private static double norme(Map<String, Double> vecteur) {
        double sommeDesCarres = 0;
        for (double poids : vecteur.values()) {
            sommeDesCarres += poids * poids;
        }
        return Math.sqrt(sommeDesCarres);
    }
}
