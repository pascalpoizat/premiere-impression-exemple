package fr.miage.premiereimpression.tenue.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import fr.miage.premiereimpression.tenue.domain.DonneesTenue;
import fr.miage.premiereimpression.tenue.domain.model.Mensurations;
import fr.miage.premiereimpression.tenue.domain.model.Mesure;
import fr.miage.premiereimpression.tenue.domain.model.NouveauVetement;
import fr.miage.premiereimpression.tenue.domain.model.ProfilId;
import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;
import fr.miage.premiereimpression.tenue.domain.model.Vetement;

/** Test d'intégration : adaptateurs JPA sur le schéma réel créé par les migrations Flyway. */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // garde la base H2 en mode PostgreSQL de src/test/resources
@Import({ MensurationsPersistenceAdapter.class, VetementPersistenceAdapter.class })
class PersistanceTenueIT {

    @Autowired
    private MensurationsPersistenceAdapter mensurations;

    @Autowired
    private VetementPersistenceAdapter vetements;

    @Autowired
    private VetementJpaRepository vetementJpa;

    @Autowired
    private JdbcTemplate jdbc;

    private ProfilId profil;

    /** Le profil appartient au module candidature : on l'insère directement en SQL. */
    @BeforeEach
    void unProfil() {
        jdbc.update("INSERT INTO profil (pseudo, competences) VALUES ('alex', '')");
        profil = new ProfilId(jdbc.queryForObject("SELECT id FROM profil WHERE pseudo = 'alex'", Long.class));
    }

    @Test
    void enregistreRelitEtRemplaceLesMensurations() {
        Mensurations premieres = DonneesTenue.corps(profil, 96, 82, 98, 80, 44);
        Mensurations secondes = DonneesTenue.corps(profil, 100, 84, 99, 81, 45);

        mensurations.enregistrer(premieres);
        mensurations.enregistrer(secondes);

        assertThat(mensurations.trouver(profil)).contains(secondes);
    }

    @Test
    void enregistreUnVetementAvecSesMesures() {
        Map<Mesure, Double> mesures = Map.of(Mesure.TOUR_TAILLE, 84.0, Mesure.TOUR_HANCHES, 102.0,
                Mesure.LONGUEUR_JAMBE, 80.0);

        Vetement vetement = vetements.ajouter(new NouveauVetement(profil, TypeVetement.PANTALON, "Pantalon gris",
                "Laine", mesures));

        assertThat(vetements.tous()).containsExactly(vetement);
        assertThat(vetements.deType(TypeVetement.PANTALON)).containsExactly(vetement);
        assertThat(vetements.deType(TypeVetement.VESTE)).isEmpty();
        assertThat(vetement.mesures()).isEqualTo(mesures);
    }

    @Test
    void leSchemaRefuseUnVetementSansProprietaire() {
        VetementJpaEntity orphelin = new VetementJpaEntity(999, TypeVetement.JUPE, "Jupe", null,
                Map.of(Mesure.TOUR_TAILLE, 70.0, Mesure.TOUR_HANCHES, 95.0));

        assertThatThrownBy(() -> vetementJpa.saveAndFlush(orphelin))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void leSchemaRefuseUneMesureNegative() {
        VetementJpaEntity invalide = new VetementJpaEntity(profil.valeur(), TypeVetement.JUPE, "Jupe", null,
                Map.of(Mesure.TOUR_TAILLE, -1.0, Mesure.TOUR_HANCHES, 95.0));

        assertThatThrownBy(() -> vetementJpa.saveAndFlush(invalide))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
