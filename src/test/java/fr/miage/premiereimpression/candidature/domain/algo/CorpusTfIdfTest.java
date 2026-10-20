package fr.miage.premiereimpression.candidature.domain.algo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class CorpusTfIdfTest {

    private final AnalyseurTexte analyseur = new AnalyseurTexte();

    private CorpusTfIdf corpus(String... documents) {
        return new CorpusTfIdf(List.of(documents).stream().map(analyseur::analyser).toList());
    }

    @Test
    void unTermePresentDansTousLesDocumentsAUnIdfMinimal() {
        CorpusTfIdf corpus = corpus("java spring", "java python", "java docker");

        assertThat(corpus.idf("java")).isEqualTo(1.0);
    }

    @Test
    void unTermePlusRareAUnIdfPlusGrand() {
        CorpusTfIdf corpus = corpus("java spring", "java python", "java docker");

        assertThat(corpus.idf("spring")).isGreaterThan(corpus.idf("java"));
    }

    @Test
    void unTermeAbsentDuCorpusAUnIdfMaximalEtFini() {
        CorpusTfIdf corpus = corpus("java spring", "java python");

        assertThat(corpus.idf("cobol")).isCloseTo(1 + Math.log(3), within(1e-12));
        assertThat(corpus.idf("cobol")).isGreaterThan(corpus.idf("spring"));
    }

    @Test
    void leTfEstNormaliseParLaLongueurDuDocument() {
        CorpusTfIdf corpus = corpus("java java spring", "java");

        Map<String, Double> vecteur = corpus.vectoriser(analyseur.analyser("java java spring"));

        assertThat(vecteur.get("java")).isCloseTo(2.0 / 3 * corpus.idf("java"), within(1e-12));
        assertThat(vecteur.get("spring")).isCloseTo(1.0 / 3 * corpus.idf("spring"), within(1e-12));
    }

    @Test
    void unDocumentVideDonneUnVecteurVide() {
        assertThat(corpus("java").vectoriser(List.of())).isEmpty();
    }

    @Test
    void unCorpusVideDonneUnIdfDeUn() {
        assertThat(corpus().idf("java")).isEqualTo(1.0);
    }
}
