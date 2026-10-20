package fr.miage.premiereimpression.candidature.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import fr.miage.premiereimpression.candidature.domain.model.Candidature;
import fr.miage.premiereimpression.candidature.domain.model.NouveauProfil;
import fr.miage.premiereimpression.candidature.domain.model.NouvelleCandidature;
import fr.miage.premiereimpression.candidature.domain.model.Profil;

/** Test d'intégration : adaptateurs JPA sur le schéma réel créé par les migrations Flyway. */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // garde la base H2 en mode PostgreSQL de src/test/resources
@Import({ ProfilPersistenceAdapter.class, CandidaturePersistenceAdapter.class })
class PersistanceCandidatureIT {

    @Autowired
    private ProfilPersistenceAdapter profils;

    @Autowired
    private CandidaturePersistenceAdapter candidatures;

    @Autowired
    private ProfilJpaRepository profilJpa;

    @Autowired
    private CandidatureJpaRepository candidatureJpa;

    @Test
    void enregistreEtRelitUnProfil() {
        Profil profil = profils.ajouter(new NouveauProfil("alex", "Java"));

        assertThat(profils.trouver(profil.id())).contains(profil);
        assertThat(profils.existePseudo("alex")).isTrue();
        assertThat(profils.existePseudo("sam")).isFalse();
    }

    @Test
    void enregistreEtRelitUneCandidatureAvecSaDate() {
        Profil profil = profils.ajouter(new NouveauProfil("alex", "Java"));
        LocalDateTime entretien = LocalDateTime.of(2026, 11, 5, 14, 30);

        Candidature candidature = candidatures.ajouter(new NouvelleCandidature(profil.id(), "ACME", "Dev",
                "Annonce Java", entretien, "Paris"));

        assertThat(candidatures.trouver(candidature.id())).contains(candidature);
        assertThat(candidature.dateHeureEntretien()).isEqualTo(entretien);
        assertThat(candidatures.deProfil(profil.id())).containsExactly(candidature);
        assertThat(candidatures.textesDesAnnonces()).containsExactly("Annonce Java");
    }

    @Test
    void conserveUneAnnonceLongue() {
        Profil profil = profils.ajouter(new NouveauProfil("alex", ""));
        String annonce = "java ".repeat(3_000);

        Candidature candidature = candidatures.ajouter(new NouvelleCandidature(profil.id(), "ACME", "Dev",
                annonce, null, null));

        assertThat(candidatures.trouver(candidature.id()).orElseThrow().texteAnnonce()).hasSize(annonce.strip().length());
    }

    @Test
    void leSchemaRefuseDeuxProfilsDeMemePseudo() {
        profilJpa.saveAndFlush(new ProfilJpaEntity("alex", ""));

        assertThatThrownBy(() -> profilJpa.saveAndFlush(new ProfilJpaEntity("alex", "")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void leSchemaRefuseUneCandidatureSansProfil() {
        CandidatureJpaEntity orpheline = new CandidatureJpaEntity(999, "ACME", "Dev", "Annonce", null, null);

        assertThatThrownBy(() -> candidatureJpa.saveAndFlush(orpheline))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
