package fr.miage.premiereimpression.candidature.domain.algo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import fr.miage.premiereimpression.candidature.domain.exception.DonneeInvalideException;
import fr.miage.premiereimpression.candidature.domain.model.AnalyseAnnonce;
import fr.miage.premiereimpression.candidature.domain.model.MotCle;

class AnalyseurAnnonceTest {

    private static final String ANNONCE_JAVA = """
            Nous recherchons un développeur Java pour concevoir des API REST avec Spring Boot.
            Vous écrirez des tests unitaires. Une expérience de Docker est appréciée. Java est indispensable.
            """;
    private static final String ANNONCE_DATA = "Analyste de données : SQL, Python, tableaux de bord Power BI.";
    private static final String ANNONCE_ERP = "Consultant ERP SAP, paramétrage des modules, Java apprécié.";
    private static final List<String> CORPUS = List.of(ANNONCE_JAVA, ANNONCE_DATA, ANNONCE_ERP);

    private final AnalyseurAnnonce analyseur = new AnalyseurAnnonce();

    private AnalyseAnnonce analyser(String profil) {
        return analyseur.analyser(ANNONCE_JAVA, profil, CORPUS, AnalyseurAnnonce.K_MAX);
    }

    @Test
    void lesMotsClesSontTriesParPoidsDecroissant() {
        List<MotCle> motsCles = analyseur.analyser(ANNONCE_JAVA, "", CORPUS, 10).motsCles();

        assertThat(motsCles).hasSize(10);
        assertThat(motsCles).extracting(MotCle::poids).isSortedAccordingTo((a, b) -> Double.compare(b, a));
    }

    @Test
    void unTermeRepeteEtRareRessortEnPremier() {
        // « spring » n'apparaît que dans cette annonce, « java » est répété mais présent ailleurs
        List<String> racines = analyser("").motsCles().stream().map(MotCle::racine).toList();

        assertThat(racines).contains("java", "spring", "docker");
        assertThat(racines.indexOf("java")).isLessThan(racines.indexOf("docker"));
    }

    @Test
    void afficheLaFormeLaPlusFrequente() {
        assertThat(analyser("").motsCles()).filteredOn(m -> m.racine().equals("developp"))
                .extracting(MotCle::forme).containsExactly("développeur");
    }

    @Test
    void unProfilPertinentCorrespondMieuxQuUnProfilSansRapport() {
        double pertinent = analyser("Développement Java, Spring Boot, API REST, tests unitaires").correspondance();
        double sansRapport = analyser("Comptabilité, paie, gestion des fournisseurs").correspondance();

        assertThat(pertinent).isGreaterThan(sansRapport);
        assertThat(sansRapport).isZero();
    }

    @Test
    void lesCompetencesATravaillerSontLesMotsClesAbsentsDuProfil() {
        AnalyseAnnonce analyse = analyser("Java et Spring");

        List<String> aTravailler = analyse.competencesATravailler().stream().map(MotCle::racine).toList();
        assertThat(aTravailler).doesNotContain("java", "spring").contains("docker");
        assertThat(analyse.motsCles()).containsAll(analyse.competencesATravailler());
    }

    @Test
    void leScoreNeDependNiDeLaCasseNiDesAccentsDuProfil() {
        assertThat(analyser("JAVA DÉVELOPPEMENT").correspondance())
                .isEqualTo(analyser("java developpement").correspondance());
    }

    @Test
    void lAnalyseEstDeterministe() {
        assertThat(analyser("Java")).isEqualTo(analyser("Java"));
    }

    @Test
    void kLimiteLeNombreDeMotsCles() {
        assertThat(analyseur.analyser(ANNONCE_JAVA, "", CORPUS, 3).motsCles()).hasSize(3);
        assertThat(analyseur.analyser("Java Spring", "", CORPUS, 50).motsCles()).hasSize(2);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -1, AnalyseurAnnonce.K_MAX + 1 })
    void refuseUnKHorsBornes(int k) {
        assertThatThrownBy(() -> analyseur.analyser(ANNONCE_JAVA, "", CORPUS, k))
                .isInstanceOf(DonneeInvalideException.class);
    }

    @Test
    void fonctionneAvecUneSeuleAnnonceEnBase() {
        AnalyseAnnonce analyse = analyseur.analyser(ANNONCE_DATA, "SQL", List.of(ANNONCE_DATA), 5);

        assertThat(analyse.motsCles()).isNotEmpty().allMatch(motCle -> motCle.poids() > 0);
        assertThat(analyse.correspondance()).isPositive();
    }
}
