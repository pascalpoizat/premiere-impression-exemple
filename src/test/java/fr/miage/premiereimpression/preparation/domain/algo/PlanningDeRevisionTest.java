package fr.miage.premiereimpression.preparation.domain.algo;

import static fr.miage.premiereimpression.preparation.domain.DonneesPreparation.c;
import static fr.miage.premiereimpression.preparation.domain.DonneesPreparation.notation;
import static fr.miage.premiereimpression.preparation.domain.algo.PlacementStrategyType.CLOSE_TO_ACHIEVEMENT;
import static fr.miage.premiereimpression.preparation.domain.algo.PlacementStrategyType.MINIMAL_KNOWLEDGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import fr.miage.premiereimpression.preparation.domain.exception.DonneeInvalideException;
import fr.miage.premiereimpression.preparation.domain.model.Bloc;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequis;
import fr.miage.premiereimpression.preparation.domain.model.Niveau;

/** Exemples E1 à E4 de la fiche F3 (déroulés à la main par le responsable) et critères d'acceptation. */
class PlanningDeRevisionTest {

    private static final List<ConceptRequis> E1 = List.of(c(1, 3, 5, 240), c(2, 1, 4, 60));
    private static final List<ConceptRequis> E2 = List.of(c(1, 3, 4, 360), c(2, 1, 5, 60));
    private static final List<ConceptRequis> E3 = List.of(c(1, 3, 4, 60), c(2, 1, 5, 120));
    private static final List<ConceptRequis> E4 = List.of(c(1, 3, 5, 240), c(2, 1, 4, 60), c(3, 0, 4, 120));

    private static String sequence(List<ConceptRequis> besoin, PlacementStrategyType strategie) {
        return notation(PlanningDeRevision.sequence(besoin, strategie.strategie()).toList());
    }

    @Test
    void e1() {
        assertThat(sequence(E1, CLOSE_TO_ACHIEVEMENT)).isEqualTo("C2→2, C2→3, C2→4, C1→4, C1→5");
        assertThat(sequence(E1, MINIMAL_KNOWLEDGE)).isEqualTo("C2→2, C2→3, C1→4, C2→4, C1→5");
    }

    @Test
    void e2() {
        assertThat(sequence(E2, CLOSE_TO_ACHIEVEMENT)).isEqualTo("C2→2, C2→3, C2→4, C2→5, C1→4");
        assertThat(sequence(E2, MINIMAL_KNOWLEDGE)).isEqualTo("C2→2, C2→3, C2→4, C1→4, C2→5");
    }

    @Test
    void e3LesDeuxStrategiesCommencentParUnConceptDifferent() {
        assertThat(sequence(E3, CLOSE_TO_ACHIEVEMENT)).isEqualTo("C1→4, C2→2, C2→3, C2→4, C2→5");
        assertThat(sequence(E3, MINIMAL_KNOWLEDGE)).isEqualTo("C2→2, C2→3, C2→4, C1→4, C2→5");
    }

    @Test
    void caPasAssezTempsE4GardeLePlusLongPrefixeQuiTientDans300Minutes() {
        List<Niveau> cta = PlanningDeRevision.prefixe(PlanningDeRevision.sequence(E4, CLOSE_TO_ACHIEVEMENT.strategie()), 300);
        List<Niveau> mkw = PlanningDeRevision.prefixe(PlanningDeRevision.sequence(E4, MINIMAL_KNOWLEDGE.strategie()), 300);

        assertThat(notation(cta)).isEqualTo("C2→2, C2→3, C2→4, C3→1");
        assertThat(notation(mkw)).isEqualTo("C3→1, C2→2, C3→2");
    }

    @Test
    void lePrefixeEstStrictIlNeSautePasLeNiveauQuiDepasse() {
        // C1→3 (240) dépasse les 100 minutes : on s'arrête, même si C2→2 (60) tiendrait encore
        List<ConceptRequis> besoin = List.of(c(1, 2, 4, 240), c(2, 1, 2, 60));

        assertThat(notation(PlanningDeRevision.sequence(besoin, MINIMAL_KNOWLEDGE.strategie()).toList()))
                .isEqualTo("C1→3, C2→2, C1→4");
        assertThat(PlanningDeRevision.prefixe(PlanningDeRevision.sequence(besoin, MINIMAL_KNOWLEDGE.strategie()), 100))
                .isEmpty();
    }

    @Test
    void lePrefixeNeCalculeQueLesNiveauxGardesPlusCeluiQuiDepasse() {
        java.util.concurrent.atomic.AtomicInteger tires = new java.util.concurrent.atomic.AtomicInteger();
        // E4 avec CTA : 4 niveaux gardés dans 300 minutes, le 5e (C3→2) fait dépasser, les 7 suivants ne sont jamais calculés
        List<Niveau> gardes = PlanningDeRevision.prefixe(
                PlanningDeRevision.sequence(E4, CLOSE_TO_ACHIEVEMENT.strategie()).peek(niveau -> tires.incrementAndGet()), 300);

        assertThat(gardes).hasSize(4);
        assertThat(tires).hasValue(5);
    }

    @Test
    void e1DecoupeEnJours() {
        List<Bloc> planning = PlanningDeRevision.planning(E1, CLOSE_TO_ACHIEVEMENT.strategie(),
                List.of(120, 90, 120, 120, 120, 120));

        assertThat(planning).extracting(b -> (b.jour() + 1) + " " + b.niveau().concept().nom() + "→"
                + b.niveau().k() + " (" + b.duree() + ")").containsExactly(
                        "1 C2→2 (60)", "1 C2→3 (60)",
                        "2 C2→4 (60)", "2 C1→4 (30)",
                        "3 C1→4 (120)",
                        "4 C1→4 (90)", "4 C1→5 (30)",
                        "5 C1→5 (120)",
                        "6 C1→5 (90)");
    }

    @Test
    void lesJoursSansDisponibiliteSontSautes() {
        List<Bloc> planning = PlanningDeRevision.planning(List.of(c(1, 0, 1, 60)), CLOSE_TO_ACHIEVEMENT.strategie(),
                List.of(0, 30, 0, 30));

        assertThat(planning).extracting(Bloc::jour, Bloc::duree).containsExactly(
                org.assertj.core.groups.Tuple.tuple(1, 30), org.assertj.core.groups.Tuple.tuple(3, 30));
    }

    @ParameterizedTest
    @EnumSource(PlacementStrategyType.class)
    void caVide1SansTempsDisponibleLePlanningEstVide(PlacementStrategyType strategie) {
        assertThat(PlanningDeRevision.planning(E4, strategie.strategie(), List.of(0, 0, 0))).isEmpty();
        assertThat(PlanningDeRevision.planning(E4, strategie.strategie(), List.of())).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(PlacementStrategyType.class)
    void caVide2SansBesoinLePlanningEstVide(PlacementStrategyType strategie) {
        List<ConceptRequis> toutEstAcquis = List.of(c(1, 5, 5, 60), c(2, 4, 2, 30));

        assertThat(PlanningDeRevision.planning(toutEstAcquis, strategie.strategie(), List.of(120, 120))).isEmpty();
        assertThat(PlanningDeRevision.planning(List.of(), strategie.strategie(), List.of(120))).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(PlacementStrategyType.class)
    void caAssezTempsLePlanningPlaceToutLePlacementDuBesoin(PlacementStrategyType strategie) {
        List<Niveau> placement = PlanningDeRevision.sequence(E4, strategie.strategie()).toList();
        // b = 480 + 180 + 480 = 1140 minutes, d = 10 × 120 = 1200
        List<Bloc> planning = PlanningDeRevision.planning(E4, strategie.strategie(), List.of(120, 120, 120, 120, 120,
                120, 120, 120, 120, 120));

        assertThat(planning.stream().map(Bloc::niveau).distinct().toList()).isEqualTo(placement);
        assertThat(planning.stream().mapToInt(Bloc::duree).sum()).isEqualTo(1140);
    }

    @Test
    void uneDisponibiliteHorsDeZeroA1440EstRefusee() {
        assertThatThrownBy(() -> PlanningDeRevision.planning(E1, CLOSE_TO_ACHIEVEMENT.strategie(), List.of(1441)))
                .isInstanceOf(DonneeInvalideException.class);
        assertThatThrownBy(() -> PlanningDeRevision.planning(E1, CLOSE_TO_ACHIEVEMENT.strategie(), List.of(-1)))
                .isInstanceOf(DonneeInvalideException.class);
    }

    @Test
    void placerPlusQueLeTempsDisponibleEstUneErreurDeProgrammation() {
        List<Niveau> tropLong = List.of(new Niveau(c(1, 0, 1, 90), 1));

        assertThatThrownBy(() -> PlanningDeRevision.placement(tropLong, List.of(60)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
