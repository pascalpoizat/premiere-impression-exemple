package fr.miage.premiereimpression.candidature.domain.algo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class SimilariteCosinusTest {

    static LongStream graines() {
        return LongStream.rangeClosed(1, 30);
    }

    /** Vecteur creux aléatoire sur un petit vocabulaire, pour que les vecteurs se recouvrent. */
    private static Map<String, Double> vecteurAleatoire(Random hasard) {
        Map<String, Double> vecteur = new HashMap<>();
        int taille = 1 + hasard.nextInt(8);
        for (int i = 0; i < taille; i++) {
            vecteur.put("t" + hasard.nextInt(12), hasard.nextDouble() * 5);
        }
        return vecteur;
    }

    @ParameterizedTest(name = "graine {0}")
    @MethodSource("graines")
    void proprietesSurDesVecteursAleatoires(long graine) {
        Random hasard = new Random(graine);
        Map<String, Double> x = vecteurAleatoire(hasard);
        Map<String, Double> y = vecteurAleatoire(hasard);

        assertThat(SimilariteCosinus.entre(x, x)).isCloseTo(1.0, within(1e-12));
        assertThat(SimilariteCosinus.entre(x, y)).isEqualTo(SimilariteCosinus.entre(y, x));
        assertThat(SimilariteCosinus.entre(x, y)).isBetween(0.0, 1.0);
    }

    @Test
    void estInvarianteParMultiplicationParUnScalaire() {
        Map<String, Double> x = Map.of("java", 1.0, "spring", 2.0);
        Map<String, Double> dixFoisX = Map.of("java", 10.0, "spring", 20.0);

        assertThat(SimilariteCosinus.entre(x, dixFoisX)).isCloseTo(1.0, within(1e-12));
    }

    @Test
    void vautZeroPourDesVecteursSansTermeCommun() {
        assertThat(SimilariteCosinus.entre(Map.of("java", 1.0), Map.of("python", 1.0))).isZero();
    }

    @Test
    void vautZeroSiUnVecteurEstNul() {
        assertThat(SimilariteCosinus.entre(Map.of(), Map.of("java", 1.0))).isZero();
        assertThat(SimilariteCosinus.entre(Map.of("java", 0.0), Map.of("java", 1.0))).isZero();
    }

    @Test
    void calculeLaValeurAttenduePourUnCasSimple() {
        // (1,1) et (1,0) font un angle de 45° : cos = 1 / sqrt(2)
        Map<String, Double> diagonale = Map.of("a", 1.0, "b", 1.0);
        Map<String, Double> axe = Map.of("a", 1.0);

        assertThat(SimilariteCosinus.entre(diagonale, axe)).isCloseTo(1 / Math.sqrt(2), within(1e-12));
    }
}
