package fr.miage.premiereimpression.preparation.adapter.in.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

/** Test d'intégration de bout en bout de F3, y compris le lien avec le module candidature. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // chaque test est annulé à la fin : les tests restent indépendants
class PreparationApiIT {

    @Autowired
    private MockMvc mockMvc;

    private long candidature;

    private long creer(String chemin, String json) throws Exception {
        String reponse = mockMvc.perform(post(chemin).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(reponse, "$.id")).longValue();
    }

    private long creerCandidature(String dateHeureEntretien) throws Exception {
        long profil = creer("/api/v1/profils", "{\"pseudo\": \"p%d\"}".formatted(System.nanoTime()));
        return creer("/api/v1/candidatures", """
                {"profilId": %d, "entreprise": "ACME", "poste": "Développeur", "texteAnnonce": "Annonce",
                 "dateHeureEntretien": %s}
                """.formatted(profil, dateHeureEntretien == null ? "null" : "\"" + dateHeureEntretien + "\""));
    }

    private String concepts(long candidatureId) {
        return "/api/v1/candidatures/%d/concepts-requis".formatted(candidatureId);
    }

    private void ajouter(String nom, int actuel, int requis, int qtn) throws Exception {
        creer(concepts(candidature), """
                {"nom": "%s", "actuel": %d, "requis": %d, "qtn": %d}
                """.formatted(nom, actuel, requis, qtn));
    }

    /** Entretien dans 3 jours : n = 3 disponibilités, d'aujourd'hui à la veille. */
    @BeforeEach
    void uneCandidatureDansTroisJours() throws Exception {
        candidature = creerCandidature(LocalDate.now().plusDays(3) + "T10:00:00");
    }

    @Test
    void ajouteUnConceptAvecLocationEtLeRangDeSaisie() throws Exception {
        ajouter("SQL", 1, 4, 60);

        mockMvc.perform(post(concepts(candidature)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\": \"Java\", \"actuel\": 3, \"requis\": 5, \"qtn\": 240}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(concepts(candidature) + "/")))
                .andExpect(jsonPath("$.rang").value(2));
        mockMvc.perform(get(concepts(candidature)))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nom").value("SQL"))
                .andExpect(jsonPath("$[1].nom").value("Java"));
    }

    @Test
    void scenarioCompletE1AvecCloseToAchievement() throws Exception {
        ajouter("C1", 3, 5, 240);
        ajouter("C2", 1, 4, 60);

        mockMvc.perform(get("/api/v1/candidatures/{id}/planning-revision", candidature)
                .param("strategie", "CLOSE_TO_ACHIEVEMENT").param("disponibilites", "120,90,120"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jours", hasSize(3)))
                .andExpect(jsonPath("$.jours[0].date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.jours[0].blocs[0].concept").value("C2"))
                .andExpect(jsonPath("$.jours[0].blocs[0].niveau").value(2))
                .andExpect(jsonPath("$.jours[1].blocs", hasSize(1)))
                .andExpect(jsonPath("$.jours[1].blocs[0].niveau").value(4))
                .andExpect(jsonPath("$.jours[2].blocs", hasSize(0)))
                // 330 minutes : C2 terminé (180), puis C1→4 (240) ne tient pas en entier, il est oublié (préfixe strict)
                .andExpect(jsonPath("$.niveauxAtteints[0].concept").value("C1"))
                .andExpect(jsonPath("$.niveauxAtteints[0].atteint").value(3))
                .andExpect(jsonPath("$.niveauxAtteints[1].atteint").value(4))
                .andExpect(jsonPath("$.tempsDisponible").value(330))
                .andExpect(jsonPath("$.tempsUtilise").value(180))
                .andExpect(jsonPath("$.tempsInutilise").value(150));
    }

    @Test
    void supprimeUnConcept() throws Exception {
        long sql = creer(concepts(candidature), "{\"nom\": \"SQL\", \"actuel\": 1, \"requis\": 4, \"qtn\": 60}");

        mockMvc.perform(delete(concepts(candidature) + "/" + sql)).andExpect(status().isNoContent());
        mockMvc.perform(delete(concepts(candidature) + "/" + sql))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Concept requis introuvable"));
    }

    @Test
    void caErreurs400PourUneSaisieInvalide() throws Exception {
        for (String invalide : new String[] {
                "{\"nom\": \"SQL\", \"actuel\": 6, \"requis\": 4, \"qtn\": 60}",
                "{\"nom\": \"SQL\", \"actuel\": 1, \"requis\": -1, \"qtn\": 60}",
                "{\"nom\": \"SQL\", \"actuel\": 1, \"requis\": 4, \"qtn\": 0}",
                "{\"nom\": \"  \", \"actuel\": 1, \"requis\": 4, \"qtn\": 60}" }) {
            mockMvc.perform(post(concepts(candidature)).contentType(MediaType.APPLICATION_JSON).content(invalide))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
        }
    }

    @Test
    void caErreurs400PourDesDisponibilitesOuUneStrategieInvalides() throws Exception {
        String planning = "/api/v1/candidatures/%d/planning-revision".formatted(candidature);

        mockMvc.perform(get(planning).param("strategie", "CLOSE_TO_ACHIEVEMENT").param("disponibilites", "60,60"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("soit 3")));
        mockMvc.perform(get(planning).param("strategie", "CLOSE_TO_ACHIEVEMENT").param("disponibilites", "60,60,1441"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get(planning).param("strategie", "AU_HASARD").param("disponibilites", "60,60,60"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get(planning).param("disponibilites", "60,60,60")).andExpect(status().isBadRequest());
    }

    @Test
    void caErreurs409PourUnNomDejaSaisi() throws Exception {
        ajouter("SQL", 1, 4, 60);

        mockMvc.perform(post(concepts(candidature)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\": \"SQL\", \"actuel\": 0, \"requis\": 2, \"qtn\": 30}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Concept déjà saisi"));
    }

    @Test
    void caErreurs404PourUneCandidatureInconnue() throws Exception {
        mockMvc.perform(get(concepts(999_999))).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Candidature introuvable"));
        mockMvc.perform(get("/api/v1/candidatures/999999/planning-revision").param("strategie", "MINIMAL_KNOWLEDGE"))
                .andExpect(status().isNotFound());
    }

    @Test
    void unEntretienSansDateDonne409() throws Exception {
        long sansDate = creerCandidature(null);

        mockMvc.perform(get("/api/v1/candidatures/{id}/planning-revision", sansDate)
                .param("strategie", "MINIMAL_KNOWLEDGE"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Entretien sans date"));
    }

    @Test
    void caVide1UnEntretienPasseDonneUnPlanningVide() throws Exception {
        long passee = creerCandidature(LocalDate.now().minusDays(2) + "T10:00:00");

        mockMvc.perform(get("/api/v1/candidatures/{id}/planning-revision", passee)
                .param("strategie", "CLOSE_TO_ACHIEVEMENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jours", hasSize(0)))
                .andExpect(jsonPath("$.tempsUtilise").value(0));
    }

    @Test
    void lesEndpointsSontDocumentesDansOpenApi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/api/v1/candidatures/{candidatureId}/planning-revision")))
                .andExpect(content().string(containsString("/api/v1/candidatures/{candidatureId}/concepts-requis")))
                .andExpect(header().string("Content-Type", endsWith("json")));
    }
}
