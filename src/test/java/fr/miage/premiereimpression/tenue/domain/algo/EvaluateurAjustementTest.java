package fr.miage.premiereimpression.tenue.domain.algo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import fr.miage.premiereimpression.tenue.domain.DonneesTenue;
import fr.miage.premiereimpression.tenue.domain.model.Ajustement;
import fr.miage.premiereimpression.tenue.domain.model.Critere;
import fr.miage.premiereimpression.tenue.domain.model.DetailMesure;
import fr.miage.premiereimpression.tenue.domain.model.Mensurations;
import fr.miage.premiereimpression.tenue.domain.model.Mesure;
import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;
import fr.miage.premiereimpression.tenue.domain.model.Verdict;
import fr.miage.premiereimpression.tenue.domain.model.Vetement;

class EvaluateurAjustementTest {

    private static final Critere POITRINE_VESTE = new Critere(Mesure.TOUR_POITRINE, 2, 10, 4, 8);

    private final EvaluateurAjustement evaluateur = new EvaluateurAjustement();
    private final Mensurations moi = DonneesTenue.moi();

    private Optional<Ajustement> evaluer(TypeVetement type, double decalage) {
        return evaluateur.evaluer(moi, DonneesTenue.vetement(1, DonneesTenue.PRETEUR, type, moi, decalage));
    }

    @ParameterizedTest
    @EnumSource(TypeVetement.class)
    void unVetementAAisanceIdealeObtientUnScoreParfait(TypeVetement type) {
        Ajustement ajustement = evaluer(type, 0).orElseThrow();

        assertThat(ajustement.score()).isEqualTo(1.0);
        assertThat(ajustement.details()).extracting(DetailMesure::verdict).containsOnly(Verdict.IDEAL);
        assertThat(ajustement.details()).hasSize(type.criteres().size());
    }

    @ParameterizedTest
    @EnumSource(TypeVetement.class)
    void leScoreDecroitQuandLeVetementSElargit(TypeVetement type) {
        double precedent = 1.0;
        for (double decalage = 0.5; decalage <= 20; decalage += 0.5) {
            double score = evaluer(type, decalage).orElseThrow().score();
            assertThat(score).isLessThan(precedent).isPositive();
            precedent = score;
        }
    }

    @ParameterizedTest
    @EnumSource(TypeVetement.class)
    void leScoreDecroitQuandLeVetementRetrecitJusquAEtreElimine(TypeVetement type) {
        double precedent = 1.0;
        double decalage = -0.25;
        Optional<Ajustement> ajustement = evaluer(type, decalage);
        while (ajustement.isPresent()) {
            assertThat(ajustement.get().score()).isLessThan(precedent);
            precedent = ajustement.get().score();
            decalage -= 0.25;
            ajustement = evaluer(type, decalage);
        }
        // la plus petite tolérance serrée des critères du type borne le rétrécissement admis
        double toleranceMin = type.criteres().stream().mapToDouble(Critere::toleranceSerree).min().orElseThrow();
        assertThat(-decalage).isGreaterThan(toleranceMin);
    }

    @Test
    void unCritereJusteALaLimiteDeToleranceEstAccepteAvecPenaliteUn() {
        // écart = aisance − tolérance = 10 − 4 = 6 cm
        DetailMesure detail = EvaluateurAjustement.evaluerCritere(POITRINE_VESTE, 96, 102).orElseThrow();

        assertThat(detail.verdict()).isEqualTo(Verdict.JUSTE);
        assertThat(detail.penalite()).isCloseTo(1.0, within(1e-12));
    }

    @Test
    void unCritereTropPetitElimineLeVetement() {
        assertThat(EvaluateurAjustement.evaluerCritere(POITRINE_VESTE, 96, 101.9)).isEmpty();
    }

    @Test
    void lAmpleurEstPenaliseeQuadratiquement() {
        // écart = 26 cm, soit 16 cm au-delà de l'aisance idéale : (16 / 8)² = 4
        DetailMesure detail = EvaluateurAjustement.evaluerCritere(POITRINE_VESTE, 96, 122).orElseThrow();

        assertThat(detail.verdict()).isEqualTo(Verdict.AMPLE);
        assertThat(detail.ecart()).isEqualTo(26);
        assertThat(detail.penalite()).isCloseTo(4.0, within(1e-12));
    }

    @Test
    void tropGrandResteAcceptableAlorsQueTropPetitNon() {
        assertThat(evaluer(TypeVetement.VESTE, 30)).isPresent();
        assertThat(evaluer(TypeVetement.VESTE, -30)).isEmpty();
    }

    @Test
    void leScoreEstLaMoyennePondereeDesPenalites() {
        // seule la carrure (poids 3 sur 6) est décalée de +3 cm : pénalité (3 / 3)² = 1
        Vetement vetement = DonneesTenue.vetement(1, DonneesTenue.PRETEUR, TypeVetement.VESTE, moi, 0);
        Map<Mesure, Double> mesures = new EnumMap<>(vetement.mesures());
        mesures.put(Mesure.CARRURE, mesures.get(Mesure.CARRURE) + 3);
        Vetement decale = new Vetement(vetement.id(), vetement.proprietaire(), vetement.type(), vetement.nom(),
                null, mesures);

        double score = evaluateur.evaluer(moi, decale).orElseThrow().score();

        assertThat(score).isCloseTo(1 / (1 + 3.0 * 1 / 6), within(1e-12));
    }
}
