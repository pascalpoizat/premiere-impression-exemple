package fr.miage.premiereimpression.candidature.adapter.in.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

/** Test d'intégration de bout en bout : HTTP → contrôleurs → services → JPA → H2 (schéma Flyway). */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // chaque test est annulé à la fin : les tests restent indépendants
class CandidatureApiIT {

    @Autowired
    private MockMvc mockMvc;

    private long creer(String url, String corps) throws Exception {
        String reponse = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(corps))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(reponse, "$.id")).longValue();
    }

    private long creerProfil(String pseudo, String competences) throws Exception {
        return creer("/api/v1/profils", """
                {"pseudo": "%s", "competences": "%s"}
                """.formatted(pseudo, competences));
    }

    private long candidater(long profil, String annonce) throws Exception {
        return creer("/api/v1/candidatures", """
                {"profilId": %d, "entreprise": "ACME", "poste": "Développeur", "texteAnnonce": "%s",
                 "dateHeureEntretien": "2026-11-05T14:30", "adresseEntretien": "Paris"}
                """.formatted(profil, annonce));
    }

    @Test
    void creerUnProfilRenvoieSonEmplacement() throws Exception {
        mockMvc.perform(post("/api/v1/profils").contentType(MediaType.APPLICATION_JSON).content("""
                {"pseudo": "alex", "competences": "Java"}
                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/profils/")))
                .andExpect(jsonPath("$.pseudo").value("alex"));
    }

    @Test
    void unPseudoDejaPrisDonne409() throws Exception {
        creerProfil("alex", "");

        mockMvc.perform(post("/api/v1/profils").contentType(MediaType.APPLICATION_JSON).content("""
                {"pseudo": "alex"}
                """))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Pseudo déjà pris"));
    }

    @Test
    void scenarioCompletAnalyseUneAnnonce() throws Exception {
        long profil = creerProfil("alex", "Java, Spring Boot, tests unitaires");
        candidater(profil, "Analyste SQL Python Power BI");
        long candidature = candidater(profil, "Développeur Java Spring Boot, Docker et Kubernetes. Java indispensable.");

        mockMvc.perform(get("/api/v1/candidatures/{id}", candidature))
                .andExpect(jsonPath("$.dateHeureEntretien").value("2026-11-05T14:30:00"));

        mockMvc.perform(get("/api/v1/candidatures/{id}/analyse", candidature).param("k", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correspondance").value(greaterThan(0.0), Double.class))
                .andExpect(jsonPath("$.motsCles", hasSize(5)))
                .andExpect(jsonPath("$.motsCles[0].terme").value("java"))
                .andExpect(jsonPath("$.competencesATravailler[*].terme", hasItem("docker")))
                .andExpect(jsonPath("$.competencesATravailler[*].terme", not(hasItem("java"))))
                .andExpect(jsonPath("$.motsCles[4].poids").value(greaterThan(0.0), Double.class));
    }

    @Test
    void listeLesCandidaturesParProfil() throws Exception {
        long alex = creerProfil("alex", "");
        long sam = creerProfil("sam", "");
        candidater(alex, "Java");
        candidater(alex, "Python");
        candidater(sam, "SQL");

        mockMvc.perform(get("/api/v1/candidatures").param("profilId", "" + alex))
                .andExpect(jsonPath("$", hasSize(2)));
        mockMvc.perform(get("/api/v1/candidatures")).andExpect(jsonPath("$", hasSize(3)));
        mockMvc.perform(get("/api/v1/profils")).andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void candidatureInconnueDonne404() throws Exception {
        mockMvc.perform(get("/api/v1/candidatures/999999/analyse"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Candidature introuvable"));
    }

    @Test
    void candidaturePourUnProfilInconnuDonne404() throws Exception {
        mockMvc.perform(post("/api/v1/candidatures").contentType(MediaType.APPLICATION_JSON).content("""
                {"profilId": 999999, "entreprise": "ACME", "poste": "Dev", "texteAnnonce": "Java"}
                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Profil introuvable"));
    }

    @Test
    void kHorsBornesDonne400() throws Exception {
        long candidature = candidater(creerProfil("alex", ""), "Java");

        mockMvc.perform(get("/api/v1/candidatures/{id}/analyse", candidature).param("k", "51"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Donnée invalide"));
    }

    @Test
    void requeteMalFormeeDonne400() throws Exception {
        mockMvc.perform(post("/api/v1/candidatures").contentType(MediaType.APPLICATION_JSON).content("""
                {"profilId": 1, "entreprise": "", "poste": "Dev", "texteAnnonce": "Java"}
                """))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/v1/candidatures").contentType(MediaType.APPLICATION_JSON).content("""
                {"profilId": 1, "entreprise": "ACME", "poste": "Dev", "texteAnnonce": "Java",
                 "dateHeureEntretien": "demain matin"}
                """))
                .andExpect(status().isBadRequest());
    }
}
