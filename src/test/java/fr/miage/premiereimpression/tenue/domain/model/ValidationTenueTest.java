package fr.miage.premiereimpression.tenue.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import fr.miage.premiereimpression.tenue.domain.DonneesTenue;
import fr.miage.premiereimpression.tenue.domain.exception.DonneeInvalideException;

class ValidationTenueTest {

    private static Map<Mesure, Double> mesuresDe(TypeVetement type, double valeur) {
        Map<Mesure, Double> mesures = new EnumMap<>(Mesure.class);
        type.mesuresRequises().forEach(mesure -> mesures.put(mesure, valeur));
        return mesures;
    }

    @Test
    void desMensurationsCompletesSontAcceptees() {
        assertThat(DonneesTenue.moi().valeur(Mesure.CARRURE)).isEqualTo(44);
    }

    @Test
    void desMensurationsIncompletesSontRefusees() {
        Map<Mesure, Double> incompletes = Map.of(Mesure.TOUR_POITRINE, 96.0);
        assertThatThrownBy(() -> new Mensurations(DonneesTenue.MOI, incompletes))
                .isInstanceOf(DonneeInvalideException.class);
    }

    @ParameterizedTest
    @ValueSource(doubles = { 24.9, 70.1, -1, Double.NaN })
    void uneCarrureImplausibleEstRefusee(double carrure) {
        assertThatThrownBy(() -> DonneesTenue.corps(DonneesTenue.MOI, 96, 82, 98, 80, carrure))
                .isInstanceOf(DonneeInvalideException.class).hasMessageContaining("CARRURE");
    }

    @Test
    void lesBornesSontIncluses() {
        assertThat(DonneesTenue.corps(DonneesTenue.MOI, 50, 40, 50, 40, 25).valeur(Mesure.CARRURE)).isEqualTo(25);
        assertThat(DonneesTenue.corps(DonneesTenue.MOI, 200, 200, 200, 130, 70).valeur(Mesure.CARRURE)).isEqualTo(70);
    }

    @ParameterizedTest
    @EnumSource(TypeVetement.class)
    void unVetementDoitAvoirExactementLesMesuresDeSonType(TypeVetement type) {
        assertThat(new NouveauVetement(DonneesTenue.MOI, type, "Ok", null, mesuresDe(type, 100)).mesures())
                .containsOnlyKeys(type.mesuresRequises());

        Map<Mesure, Double> avecUneEnTrop = mesuresDe(type, 100);
        Mesure enTrop = Arrays.stream(Mesure.values())
                .filter(mesure -> !type.mesuresRequises().contains(mesure)).findFirst().orElseThrow();
        avecUneEnTrop.put(enTrop, 100.0);
        assertThatThrownBy(() -> new NouveauVetement(DonneesTenue.MOI, type, "Trop", null, avecUneEnTrop))
                .isInstanceOf(DonneeInvalideException.class);
    }

    @Test
    void unVetementIncompletEstRefuse() {
        Map<Mesure, Double> incompletes = Map.of(Mesure.CARRURE, 46.0);
        assertThatThrownBy(() -> new NouveauVetement(DonneesTenue.MOI, TypeVetement.VESTE, "Veste", null, incompletes))
                .isInstanceOf(DonneeInvalideException.class).hasMessageContaining("VESTE");
    }

    @ParameterizedTest
    @ValueSource(doubles = { 0, -5, 300.1 })
    void uneMesureDeVetementHorsBornesEstRefusee(double valeur) {
        Map<Mesure, Double> mesures = mesuresDe(TypeVetement.JUPE, 90);
        mesures.put(Mesure.TOUR_TAILLE, valeur);
        assertThatThrownBy(() -> new NouveauVetement(DonneesTenue.MOI, TypeVetement.JUPE, "Jupe", null, mesures))
                .isInstanceOf(DonneeInvalideException.class);
    }

    @Test
    void leNomEstObligatoireEtLaDescriptionFacultative() {
        Map<Mesure, Double> mesures = mesuresDe(TypeVetement.JUPE, 90);
        assertThatThrownBy(() -> new NouveauVetement(DonneesTenue.MOI, TypeVetement.JUPE, " ", null, mesures))
                .isInstanceOf(DonneeInvalideException.class);
        assertThat(new NouveauVetement(DonneesTenue.MOI, TypeVetement.JUPE, " Jupe ", "  ", mesures))
                .extracting(NouveauVetement::nom, NouveauVetement::description).containsExactly("Jupe", null);
    }
}
