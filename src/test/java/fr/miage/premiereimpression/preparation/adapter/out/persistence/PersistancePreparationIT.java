package fr.miage.premiereimpression.preparation.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequis;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequisId;
import fr.miage.premiereimpression.preparation.domain.model.NouveauConceptRequis;

/** Test d'intégration : adaptateur JPA sur le schéma réel créé par les migrations Flyway (V4). */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // garde la base H2 en mode PostgreSQL de src/test/resources
@Import(ConceptRequisPersistenceAdapter.class)
class PersistancePreparationIT {

    @Autowired
    private ConceptRequisPersistenceAdapter concepts;

    @Autowired
    private ConceptRequisJpaRepository jpa;

    @Autowired
    private JdbcTemplate jdbc;

    private CandidatureId candidature;

    /** La candidature appartient au module candidature : on l'insère directement en SQL. */
    @BeforeEach
    void uneCandidature() {
        jdbc.update("INSERT INTO profil (pseudo, competences) VALUES ('alex', '')");
        jdbc.update("""
                INSERT INTO candidature (profil_id, entreprise, poste, texte_annonce)
                SELECT id, 'ACME', 'Développeur', 'Annonce' FROM profil WHERE pseudo = 'alex'""");
        candidature = new CandidatureId(jdbc.queryForObject("SELECT max(id) FROM candidature", Long.class));
    }

    private ConceptRequis ajouter(String nom, int rang) {
        return concepts.ajouter(new NouveauConceptRequis(candidature, nom, 1, 4, 60, rang));
    }

    @Test
    void enregistreEtRelitParRangCroissant() {
        ajouter("Java", 2);
        ajouter("SQL", 1);

        assertThat(concepts.deCandidature(candidature)).extracting(ConceptRequis::nom).containsExactly("SQL", "Java");
        assertThat(concepts.rangMax(candidature)).isEqualTo(2);
        assertThat(concepts.nomDejaSaisi(candidature, "SQL")).isTrue();
        assertThat(concepts.nomDejaSaisi(candidature, "Docker")).isFalse();
    }

    @Test
    void sansConceptLeRangMaxVautZero() {
        assertThat(concepts.rangMax(candidature)).isZero();
    }

    @Test
    void supprimeSeulementUnConceptDeLaCandidature() {
        ConceptRequis sql = ajouter("SQL", 1);

        assertThat(concepts.supprimer(new CandidatureId(candidature.valeur() + 1), sql.id())).isFalse();
        assertThat(concepts.supprimer(candidature, new ConceptRequisId(sql.id().valeur() + 1))).isFalse();
        assertThat(concepts.supprimer(candidature, sql.id())).isTrue();
        assertThat(concepts.deCandidature(candidature)).isEmpty();
    }

    @Test
    void leSchemaRefuseUnNomOuUnRangEnDoublePourUneCandidature() {
        ajouter("SQL", 1);

        assertThatThrownBy(() -> jpa.saveAndFlush(new ConceptRequisJpaEntity(candidature.valeur(), "SQL", 0, 2, 30, 2)))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jpa.saveAndFlush(new ConceptRequisJpaEntity(candidature.valeur(), "Java", 0, 2, 30, 1)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void leSchemaRefuseDesNiveauxHorsBornesEtUnCoutNul() {
        assertThatThrownBy(() -> jpa.saveAndFlush(new ConceptRequisJpaEntity(candidature.valeur(), "A", 6, 2, 30, 1)))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jpa.saveAndFlush(new ConceptRequisJpaEntity(candidature.valeur(), "B", 0, 2, 0, 2)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void leSchemaRefuseUneCandidatureInexistante() {
        assertThatThrownBy(() -> jpa.saveAndFlush(new ConceptRequisJpaEntity(999_999, "SQL", 0, 2, 30, 1)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
