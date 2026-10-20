package fr.miage.premiereimpression.candidature.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import fr.miage.premiereimpression.candidature.domain.exception.DonneeInvalideException;

class ValidationModeleTest {

    private static final ProfilId PROFIL = new ProfilId(1);

    @Test
    void unProfilSansCompetencesEstAccepte() {
        assertThat(new NouveauProfil("  alex ", null)).isEqualTo(new NouveauProfil("alex", ""));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  " })
    void unPseudoEstObligatoire(String pseudo) {
        assertThatThrownBy(() -> new NouveauProfil(pseudo, "java")).isInstanceOf(DonneeInvalideException.class);
    }

    @Test
    void unPseudoTropLongEstRefuse() {
        String pseudo = "x".repeat(NouveauProfil.LONGUEUR_MAX_PSEUDO + 1);
        assertThatThrownBy(() -> new NouveauProfil(pseudo, "")).isInstanceOf(DonneeInvalideException.class);
    }

    @Test
    void uneCandidatureExigeEntreprisePosteEtAnnonce() {
        assertThatThrownBy(() -> new NouvelleCandidature(PROFIL, " ", "Dev", "Annonce", null, null))
                .isInstanceOf(DonneeInvalideException.class).hasMessageContaining("entreprise");
        assertThatThrownBy(() -> new NouvelleCandidature(PROFIL, "ACME", "Dev", "", null, null))
                .isInstanceOf(DonneeInvalideException.class).hasMessageContaining("annonce");
    }

    @Test
    void lAdresseVideDevientAbsente() {
        NouvelleCandidature candidature = new NouvelleCandidature(PROFIL, "ACME", "Dev", "Annonce", null, "  ");
        assertThat(candidature.adresseEntretien()).isNull();
    }

    @Test
    void uneAnnonceTropLongueEstRefusee() {
        String annonce = "x".repeat(NouvelleCandidature.LONGUEUR_MAX_ANNONCE + 1);
        assertThatThrownBy(() -> new NouvelleCandidature(PROFIL, "ACME", "Dev", annonce, null, null))
                .isInstanceOf(DonneeInvalideException.class);
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, -3 })
    void lesIdentifiantsSontStrictementPositifs(long valeur) {
        assertThatThrownBy(() -> new ProfilId(valeur)).isInstanceOf(DonneeInvalideException.class);
        assertThatThrownBy(() -> new CandidatureId(valeur)).isInstanceOf(DonneeInvalideException.class);
    }
}
