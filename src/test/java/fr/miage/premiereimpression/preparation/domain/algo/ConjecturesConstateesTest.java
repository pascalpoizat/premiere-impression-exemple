package fr.miage.premiereimpression.preparation.domain.algo;

import static fr.miage.premiereimpression.preparation.domain.algo.PlanningDeRevisionProprietesTest.GRAINE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.ToIntFunction;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import fr.miage.premiereimpression.preparation.domain.model.ConceptRequis;
import fr.miage.premiereimpression.preparation.domain.model.Niveau;

/**
 * Les deux conjectures de la fiche F3, constatées (et non exigées) : chaque stratégie, après coupure,
 * fait au moins aussi bien que n'importe quel planning dans le même budget, sur le critère qu'elle sert.
 * Oracle : recherche exhaustive sur tous les vecteurs (x₁, …, xₘ), 0 ≤ xᵢ ≤ nrᵢ, Σ xᵢ · qtnᵢ ≤ d.
 */
class ConjecturesConstateesTest {

    @Provide
    static Arbitrary<List<ConceptRequis>> besoins() {
        return PlanningDeRevisionProprietesTest.besoins();
    }

    @Provide
    static Arbitrary<Integer> budgets() {
        return net.jqwik.api.Arbitraries.integers().between(0, 1500);
    }

    @Property(seed = GRAINE, tries = 300)
    void ctaMaximiseLeNombreDeConceptsAmenesAuNiveauRequis(@ForAll("besoins") List<ConceptRequis> besoin,
            @ForAll("budgets") int d) {
        int[] x = montees(besoin, PlacementStrategyType.CLOSE_TO_ACHIEVEMENT, d);

        assertThat(objectifsAtteints(besoin, x)).isEqualTo(meilleur(besoin, d, v -> objectifsAtteints(besoin, v)));
    }

    @Property(seed = GRAINE, tries = 300)
    void mkwMinimiseLePlusGrandNombreDeNiveauxRestants(@ForAll("besoins") List<ConceptRequis> besoin,
            @ForAll("budgets") int d) {
        int[] x = montees(besoin, PlacementStrategyType.MINIMAL_KNOWLEDGE, d);

        // on maximise l'opposé du plus grand reste
        assertThat(-plusGrandReste(besoin, x)).isEqualTo(meilleur(besoin, d, v -> -plusGrandReste(besoin, v)));
    }

    /** xᵢ : niveaux montés sur chaque concept par la stratégie, après coupure au budget d. */
    private static int[] montees(List<ConceptRequis> besoin, PlacementStrategyType strategie, int d) {
        int[] x = new int[besoin.size()];
        for (Niveau niveau : PlanningDeRevision.prefixe(PlanningDeRevision.sequence(besoin, strategie.strategie()), d)) {
            x[besoin.indexOf(niveau.concept())]++;
        }
        return x;
    }

    private static int objectifsAtteints(List<ConceptRequis> besoin, int[] x) {
        int atteints = 0;
        for (int i = 0; i < x.length; i++) {
            if (besoin.get(i).nr() > 0 && x[i] == besoin.get(i).nr()) {
                atteints++;
            }
        }
        return atteints;
    }

    private static int plusGrandReste(List<ConceptRequis> besoin, int[] x) {
        int max = 0;
        for (int i = 0; i < x.length; i++) {
            max = Math.max(max, besoin.get(i).nr() - x[i]);
        }
        return max;
    }

    private static int meilleur(List<ConceptRequis> besoin, int d, ToIntFunction<int[]> score) {
        return explorer(besoin, d, score, new int[besoin.size()], 0, 0);
    }

    private static int explorer(List<ConceptRequis> besoin, int d, ToIntFunction<int[]> score, int[] x, int i,
            long cout) {
        if (i == x.length) {
            return score.applyAsInt(x);
        }
        int meilleur = Integer.MIN_VALUE;
        ConceptRequis concept = besoin.get(i);
        for (int xi = 0; xi <= concept.nr() && cout + (long) xi * concept.qtn() <= d; xi++) {
            x[i] = xi;
            meilleur = Math.max(meilleur, explorer(besoin, d, score, x, i + 1, cout + (long) xi * concept.qtn()));
        }
        x[i] = 0;
        return meilleur;
    }
}
