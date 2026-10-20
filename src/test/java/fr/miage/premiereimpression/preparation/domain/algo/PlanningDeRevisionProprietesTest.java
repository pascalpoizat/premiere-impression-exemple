package fr.miage.premiereimpression.preparation.domain.algo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import fr.miage.premiereimpression.preparation.domain.DonneesPreparation;
import fr.miage.premiereimpression.preparation.domain.model.Bloc;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequis;
import fr.miage.premiereimpression.preparation.domain.model.Niveau;

/**
 * Invariants de la fiche F3 (INV_*, CA_niveau_entier), vérifiés sur des données aléatoires pour les deux stratégies.
 * Générateurs fixés par le responsable : 1 à 5 concepts, niveaux 0..5, qtn de 15 à 240 minutes,
 * 0 à 10 jours de 0 à 180 minutes. Graine fixe : un échec est reproductible.
 */
class PlanningDeRevisionProprietesTest {

    static final String GRAINE = "20261020";
    static final int ESSAIS = 500;

    @Provide
    static Arbitrary<List<ConceptRequis>> besoins() {
        Arbitrary<int[]> concept = Combinators.combine(Arbitraries.integers().between(0, 5),
                Arbitraries.integers().between(0, 5), Arbitraries.integers().between(15, 240))
                .as((actuel, requis, qtn) -> new int[] { actuel, requis, qtn });
        return concept.list().ofMinSize(1).ofMaxSize(5).map(PlanningDeRevisionProprietesTest::numeroter);
    }

    private static List<ConceptRequis> numeroter(List<int[]> valeurs) {
        List<ConceptRequis> concepts = new ArrayList<>();
        for (int i = 0; i < valeurs.size(); i++) {
            int[] v = valeurs.get(i);
            concepts.add(DonneesPreparation.c(i + 1, v[0], v[1], v[2]));
        }
        return concepts;
    }

    @Provide
    static Arbitrary<List<Integer>> disponibilites() {
        return Arbitraries.integers().between(0, 180).list().ofMinSize(0).ofMaxSize(10);
    }

    private static List<Bloc> planning(List<ConceptRequis> besoin, PlacementStrategyType strategie, List<Integer> dispo) {
        return PlanningDeRevision.planning(besoin, strategie.strategie(), dispo);
    }

    private static int total(List<Bloc> blocs) {
        return blocs.stream().mapToInt(Bloc::duree).sum();
    }

    @Property(seed = GRAINE, tries = ESSAIS)
    void invPasPlusQueDispoParJour(@ForAll("besoins") List<ConceptRequis> besoin,
            @ForAll PlacementStrategyType strategie, @ForAll("disponibilites") List<Integer> dispo) {
        Map<Integer, Integer> parJour = new HashMap<>();
        planning(besoin, strategie, dispo).forEach(b -> parJour.merge(b.jour(), b.duree(), Integer::sum));

        parJour.forEach((jour, duree) -> assertThat(duree).isLessThanOrEqualTo(dispo.get(jour)));
    }

    @Property(seed = GRAINE, tries = ESSAIS)
    void invOrdreMonteeEtBornesNiveau(@ForAll("besoins") List<ConceptRequis> besoin,
            @ForAll PlacementStrategyType strategie, @ForAll("disponibilites") List<Integer> dispo) {
        Map<ConceptRequis, Integer> dernierNiveau = new HashMap<>();
        for (Bloc bloc : planning(besoin, strategie, dispo)) {
            ConceptRequis concept = bloc.niveau().concept();
            int precedent = dernierNiveau.getOrDefault(concept, concept.actuel());
            // projeté sur un concept : on reste au même niveau, ou on monte d'exactement un niveau
            assertThat(bloc.niveau().k()).isBetween(precedent, precedent + 1);
            assertThat(bloc.niveau().k()).isGreaterThan(concept.actuel()).isLessThanOrEqualTo(concept.requis());
            dernierNiveau.put(concept, bloc.niveau().k());
        }
    }

    @Property(seed = GRAINE, tries = ESSAIS)
    void invSommeMonteeUnNiveauPlaceEstPlaceEnEntier(@ForAll("besoins") List<ConceptRequis> besoin,
            @ForAll PlacementStrategyType strategie, @ForAll("disponibilites") List<Integer> dispo) {
        Map<Niveau, Integer> parNiveau = new HashMap<>();
        planning(besoin, strategie, dispo).forEach(b -> parNiveau.merge(b.niveau(), b.duree(), Integer::sum));

        parNiveau.forEach((niveau, duree) -> assertThat(duree).isEqualTo(niveau.cout()));
    }

    @Property(seed = GRAINE, tries = ESSAIS)
    void caNiveauEntierAucunNiveauNeSIntercaleEntreLesBlocsDUnAutre(@ForAll("besoins") List<ConceptRequis> besoin,
            @ForAll PlacementStrategyType strategie, @ForAll("disponibilites") List<Integer> dispo) {
        List<Niveau> ordre = planning(besoin, strategie, dispo).stream().map(Bloc::niveau).toList();
        Set<Niveau> termines = new LinkedHashSet<>();
        for (int i = 1; i < ordre.size(); i++) {
            if (!ordre.get(i).equals(ordre.get(i - 1))) {
                termines.add(ordre.get(i - 1));
                assertThat(termines).doesNotContain(ordre.get(i));
            }
        }
    }

    @Property(seed = GRAINE, tries = ESSAIS)
    void invTassementEtJoursCroissants(@ForAll("besoins") List<ConceptRequis> besoin,
            @ForAll PlacementStrategyType strategie, @ForAll("disponibilites") List<Integer> dispo) {
        List<Bloc> blocs = planning(besoin, strategie, dispo);
        int[] utilise = new int[dispo.size()];
        for (int i = 0; i < blocs.size(); i++) {
            utilise[blocs.get(i).jour()] += blocs.get(i).duree();
            if (i > 0) {
                assertThat(blocs.get(i).jour()).isGreaterThanOrEqualTo(blocs.get(i - 1).jour());
            }
        }
        int dernierJour = blocs.isEmpty() ? -1 : blocs.get(blocs.size() - 1).jour();
        for (int j = 0; j < dernierJour; j++) {
            assertThat(utilise[j]).as("jour %d non plein avant un bloc plus tardif", j).isEqualTo(dispo.get(j));
        }
    }

    @Property(seed = GRAINE, tries = ESSAIS)
    void invBudgetEtPrefixeMaximal(@ForAll("besoins") List<ConceptRequis> besoin,
            @ForAll PlacementStrategyType strategie, @ForAll("disponibilites") List<Integer> dispo) {
        long d = dispo.stream().mapToLong(Integer::longValue).sum();
        long b = besoin.stream().mapToLong(ConceptRequis::reste).sum();
        List<Niveau> sequence = PlanningDeRevision.sequence(besoin, strategie.strategie()).toList();
        List<Bloc> blocs = planning(besoin, strategie, dispo);
        int total = total(blocs);
        long placesNiveaux = blocs.stream().map(Bloc::niveau).distinct().count();

        assertThat((long) total).isLessThanOrEqualTo(Math.min(d, b));
        if (total < b) {
            Niveau suivant = sequence.get((int) placesNiveaux);
            assertThat((long) total + suivant.cout()).isGreaterThan(d);
        }
    }

    @Property(seed = GRAINE, tries = ESSAIS)
    void invUniciteBloc(@ForAll("besoins") List<ConceptRequis> besoin,
            @ForAll PlacementStrategyType strategie, @ForAll("disponibilites") List<Integer> dispo) {
        List<Bloc> blocs = planning(besoin, strategie, dispo);

        assertThat(blocs.stream().map(b -> List.of(b.jour(), b.niveau())).distinct().count())
                .isEqualTo(blocs.size());
    }

    @Property(seed = GRAINE, tries = ESSAIS)
    void invDeterminismeMemeResultatQuelQueSoitLOrdreDeLaListe(@ForAll("besoins") List<ConceptRequis> besoin,
            @ForAll PlacementStrategyType strategie, @ForAll("disponibilites") List<Integer> dispo,
            @ForAll long graineMelange) {
        List<ConceptRequis> melange = new ArrayList<>(besoin);
        Collections.shuffle(melange, new Random(graineMelange));

        assertThat(planning(melange, strategie, dispo)).isEqualTo(planning(besoin, strategie, dispo));
        assertThat(planning(besoin, strategie, dispo)).isEqualTo(planning(besoin, strategie, dispo));
    }
}
