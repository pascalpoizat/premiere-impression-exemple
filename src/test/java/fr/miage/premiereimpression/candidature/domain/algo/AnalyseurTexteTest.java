package fr.miage.premiereimpression.candidature.domain.algo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class AnalyseurTexteTest {

    private final AnalyseurTexte analyseur = new AnalyseurTexte();

    private List<String> racines(String texte) {
        return analyseur.analyser(texte).stream().map(Terme::racine).toList();
    }

    @Test
    void neDependNiDeLaCasseNiDesAccents() {
        assertThat(racines("DÉVELOPPEUR Java Sécurité")).isEqualTo(racines("developpeur java securite"));
    }

    @Test
    void retireLesMotsVidesEtLeVocabulaireGeneriqueDesAnnonces() {
        assertThat(racines("Nous recherchons un développeur pour notre équipe et the team")).containsExactly(
                "developp", "team");
    }

    @Test
    void conserveLesLangagesAvecSymboles() {
        assertThat(racines("C++, C# et Java8")).containsExactly("c++", "c#", "java8");
    }

    @Test
    void ignoreLesNombresSeulsEtLesLettresIsolees() {
        assertThat(racines("Salaire 35 000 euros, poste H/F, l'entreprise")).containsExactly("salair", "euro");
    }

    @Test
    void conserveLaFormeOriginalePourLAffichage() {
        assertThat(analyseur.analyser("Développeurs")).containsExactly(new Terme("developp", "développeurs"));
    }

    @Test
    void eclateLesLigatures() {
        assertThat(AnalyseurTexte.sansAccents("cœur")).isEqualTo("coeur");
        assertThat(AnalyseurTexte.sansAccents("ex æquo")).isEqualTo("ex aequo");
    }

    @Test
    void unTexteVideOuAbsentNeDonneAucunTerme() {
        assertThat(analyseur.analyser("")).isEmpty();
        assertThat(analyseur.analyser(null)).isEmpty();
        assertThat(analyseur.analyser(" ,;- ")).isEmpty();
    }
}
