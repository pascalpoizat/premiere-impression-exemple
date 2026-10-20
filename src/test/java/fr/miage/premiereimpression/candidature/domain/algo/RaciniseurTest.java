package fr.miage.premiereimpression.candidature.domain.algo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class RaciniseurTest {

    @ParameterizedTest
    @ValueSource(strings = { "developpement", "developpements", "developpeur", "developpeurs", "developper",
            "developpeuse", "developperez" })
    void laFamilleDeDevelopperALaMemeRacine(String mot) {
        assertThat(Raciniseur.raciner(mot)).isEqualTo("developp");
    }

    @ParameterizedTest
    @CsvSource({ "tests, test", "outils, outil", "travaux, travau", "process, process", "bus, bus" })
    void retireLeplurielSaufDoubleSEtMotsCourts(String mot, String attendu) {
        assertThat(Raciniseur.raciner(mot)).isEqualTo(attendu);
    }

    @Test
    void neRaccourcitPasLesMotsAnglaisEnEr() {
        assertThat(Raciniseur.raciner("docker")).isEqualTo("docker");
    }

    @Test
    void choisitLeSuffixeLePlusLong() {
        // « ement » est préféré à « ment » puis à « e »
        assertThat(Raciniseur.raciner("deploiement")).isEqualTo("deploi");
    }

    @Test
    void garderAuMoinsTroisLettres() {
        // « site » moins « ite » laisserait « s » : on essaie alors le suffixe suivant, « e »
        assertThat(Raciniseur.raciner("site")).isEqualTo("sit");
        assertThat(Raciniseur.raciner("api")).isEqualTo("api");
    }

    @Test
    void estIdempotenteSurUneRacineSansSuffixe() {
        assertThat(Raciniseur.raciner(Raciniseur.raciner("java"))).isEqualTo("java");
    }
}
