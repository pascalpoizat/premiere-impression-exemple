package fr.miage.premiereimpression.candidature.domain.algo;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Transforme un texte libre en liste de termes comparables :
 * minuscules, découpe, suppression des accents, des mots vides, puis racinisation.
 * Complexité linéaire en la longueur du texte.
 */
public final class AnalyseurTexte {

    /** Séparateurs : tout sauf lettres, chiffres, « + » et « # » (pour garder c++ et c#). */
    private static final Pattern SEPARATEURS = Pattern.compile("[^\\p{L}\\p{N}+#]+");
    private static final Pattern MARQUES_DIACRITIQUES = Pattern.compile("\\p{M}+");
    private static final Pattern LETTRES_SEULEMENT = Pattern.compile("[a-z]+");
    private static final Pattern AU_MOINS_UNE_LETTRE = Pattern.compile(".*[a-z].*");

    public List<Terme> analyser(String texte) {
        List<Terme> termes = new ArrayList<>();
        if (texte == null) {
            return termes;
        }
        for (String forme : SEPARATEURS.split(texte.toLowerCase(Locale.ROOT))) {
            String mot = sansAccents(forme);
            if (estSignificatif(mot)) {
                termes.add(new Terme(raciner(mot), forme));
            }
        }
        return termes;
    }

    /** « Développeur » → « developpeur », « cœur » → « coeur ». */
    static String sansAccents(String mot) {
        String ligaturesEclatees = mot.replace("œ", "oe").replace("æ", "ae");
        return MARQUES_DIACRITIQUES.matcher(Normalizer.normalize(ligaturesEclatees, Normalizer.Form.NFD))
                .replaceAll("");
    }

    private static boolean estSignificatif(String mot) {
        return mot.length() >= 2 && AU_MOINS_UNE_LETTRE.matcher(mot).matches() && !MotsVides.contient(mot);
    }

    /** Seuls les mots purement alphabétiques sont racinisés : « java8 » ou « c++ » restent intacts. */
    private static String raciner(String mot) {
        return LETTRES_SEULEMENT.matcher(mot).matches() ? Raciniseur.raciner(mot) : mot;
    }
}
