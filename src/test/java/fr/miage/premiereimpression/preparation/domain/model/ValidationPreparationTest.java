package fr.miage.premiereimpression.preparation.domain.model;

import static fr.miage.premiereimpression.preparation.domain.DonneesPreparation.CANDIDATURE;
import static fr.miage.premiereimpression.preparation.domain.DonneesPreparation.c;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import fr.miage.premiereimpression.preparation.domain.exception.DonneeInvalideException;

/** Invariants vérifiés à la construction (fail fast) ; ce sont les cas 400 de CA_erreurs. */
class ValidationPreparationTest {

    @ParameterizedTest
    @ValueSource(ints = { -1, 6 })
    void unNiveauHorsDeZeroACinqEstRefuse(int niveau) {
        assertThatThrownBy(() -> new NouveauConceptRequis(CANDIDATURE, "SQL", niveau, 3, 60, 1))
                .isInstanceOf(DonneeInvalideException.class);
        assertThatThrownBy(() -> new NouveauConceptRequis(CANDIDATURE, "SQL", 0, niveau, 60, 1))
                .isInstanceOf(DonneeInvalideException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -30 })
    void unCoutParNiveauNulOuNegatifEstRefuse(int qtn) {
        assertThatThrownBy(() -> new NouveauConceptRequis(CANDIDATURE, "SQL", 0, 3, qtn, 1))
                .isInstanceOf(DonneeInvalideException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "   " })
    void unNomVideEstRefuse(String nom) {
        assertThatThrownBy(() -> new NouveauConceptRequis(CANDIDATURE, nom, 0, 3, 60, 1))
                .isInstanceOf(DonneeInvalideException.class);
    }

    @Test
    void leNomEstNettoye() {
        assertThat(new NouveauConceptRequis(CANDIDATURE, "  SQL ", 0, 3, 60, 1).nom()).isEqualTo("SQL");
    }

    @Test
    void unConceptDejaAuNiveauEstAccepteEtNaPlusRienARemonter() {
        ConceptRequis acquis = c(1, 4, 2, 60);

        assertThat(acquis.nr()).isZero();
        assertThat(acquis.reste()).isZero();
    }

    @Test
    void nrEtResteSuiventLesNotationsDeLaFiche() {
        ConceptRequis c1 = c(1, 3, 5, 240);

        assertThat(c1.nr()).isEqualTo(2);
        assertThat(c1.reste()).isEqualTo(480);
        assertThat(c1.unNiveauPlusHaut().nr()).isEqualTo(1);
    }

    @Test
    void invBornesNiveauUnNiveauHorsDeActuelExcluARequisInclusNePeutPasExister() {
        ConceptRequis c1 = c(1, 3, 5, 240);

        assertThat(new Niveau(c1, 4).cout()).isEqualTo(240);
        assertThat(new Niveau(c1, 5).k()).isEqualTo(5);
        assertThatThrownBy(() -> new Niveau(c1, 3)).isInstanceOf(DonneeInvalideException.class);
        assertThatThrownBy(() -> new Niveau(c1, 6)).isInstanceOf(DonneeInvalideException.class);
    }

    @Test
    void unBlocADureeStrictementPositiveEtUnJourPositifOuNul() {
        Niveau niveau = new Niveau(c(1, 3, 5, 240), 4);

        assertThat(new Bloc(0, niveau, 1).duree()).isEqualTo(1);
        assertThatThrownBy(() -> new Bloc(0, niveau, 0)).isInstanceOf(DonneeInvalideException.class);
        assertThatThrownBy(() -> new Bloc(-1, niveau, 30)).isInstanceOf(DonneeInvalideException.class);
    }
}
