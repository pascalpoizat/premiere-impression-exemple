package fr.miage.premiereimpression.tenue.adapter.in.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

/** Test d'intégration de bout en bout, y compris le lien entre les modules tenue et candidature. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // chaque test est annulé à la fin : les tests restent indépendants
class TenueApiIT {

    private static final String MES_MENSURATIONS = """
            {"tourPoitrine": 96, "tourTaille": 82, "tourHanches": 98, "longueurJambe": 80, "carrure": 44}
            """;

    @Autowired
    private MockMvc mockMvc;

    private long moi;
    private long preteur;

    private long creerProfil(String pseudo) throws Exception {
        String reponse = mockMvc.perform(post("/api/v1/profils").contentType(MediaType.APPLICATION_JSON)
                .content("{\"pseudo\": \"%s\"}".formatted(pseudo)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(reponse, "$.id")).longValue();
    }

    @BeforeEach
    void deuxProfils() throws Exception {
        moi = creerProfil("moi");
        preteur = creerProfil("preteur");
    }

    private void declarerVeste(long proprietaire, String nom, double carrure, double poitrine, double taille)
            throws Exception {
        mockMvc.perform(post("/api/v1/vetements").contentType(MediaType.APPLICATION_JSON).content("""
                {"proprietaireId": %d, "type": "VESTE", "nom": "%s",
                 "mesures": {"CARRURE": %s, "TOUR_POITRINE": %s, "TOUR_TAILLE": %s}}
                """.formatted(proprietaire, nom, carrure, poitrine, taille)))
                .andExpect(status().isCreated());
    }

    @Test
    void enregistreEtRelitLesMensurations() throws Exception {
        mockMvc.perform(put("/api/v1/profils/{id}/mensurations", moi).contentType(MediaType.APPLICATION_JSON)
                .content(MES_MENSURATIONS))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/profils/{id}/mensurations", moi))
                .andExpect(jsonPath("$.carrure").value(44.0))
                .andExpect(jsonPath("$.profilId").value(moi));
    }

    @Test
    void scenarioCompletProposeLesVestesDuMeilleurAuMoinsBon() throws Exception {
        mockMvc.perform(put("/api/v1/profils/{id}/mensurations", moi).contentType(MediaType.APPLICATION_JSON)
                .content(MES_MENSURATIONS));
        declarerVeste(preteur, "Veste ample", 47, 112, 98);
        declarerVeste(preteur, "Veste parfaite", 45, 106, 92);
        declarerVeste(preteur, "Veste trop petite", 40, 96, 82);
        declarerVeste(moi, "Ma propre veste", 45, 106, 92);

        mockMvc.perform(get("/api/v1/profils/{id}/vetements-compatibles", moi).param("type", "VESTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].vetement.nom").value("Veste parfaite"))
                .andExpect(jsonPath("$[0].score").value(1.0))
                .andExpect(jsonPath("$[0].details", hasSize(3)))
                .andExpect(jsonPath("$[1].vetement.nom").value("Veste ample"))
                .andExpect(jsonPath("$[1].details[0].verdict").value("AMPLE"));
    }

    @Test
    void listeLesVetementsParTypeEtExposeLesRegles() throws Exception {
        declarerVeste(preteur, "Veste", 45, 106, 92);

        mockMvc.perform(get("/api/v1/vetements").param("type", "VESTE")).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get("/api/v1/vetements").param("type", "JUPE")).andExpect(jsonPath("$", hasSize(0)));
        mockMvc.perform(get("/api/v1/vetements/types"))
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].type").value("VESTE"))
                .andExpect(jsonPath("$[0].criteres[0].mesure").value("CARRURE"));
    }

    @Test
    void desMensurationsImplausiblesDonnent400() throws Exception {
        mockMvc.perform(put("/api/v1/profils/{id}/mensurations", moi).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"tourPoitrine": 96, "tourTaille": 82, "tourHanches": 98, "longueurJambe": 80, "carrure": 150}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail", containsString("CARRURE")));
    }

    @Test
    void unVetementSansLesBonnesMesuresDonne400() throws Exception {
        mockMvc.perform(post("/api/v1/vetements").contentType(MediaType.APPLICATION_JSON).content("""
                {"proprietaireId": %d, "type": "PANTALON", "nom": "Pantalon", "mesures": {"CARRURE": 45}}
                """.formatted(preteur)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Donnée invalide"));
    }

    @Test
    void sansMensurationsLaRechercheDonne404() throws Exception {
        mockMvc.perform(get("/api/v1/profils/{id}/vetements-compatibles", moi))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Mensurations introuvables"));
    }

    @Test
    void unProfilInconnuDonne404() throws Exception {
        mockMvc.perform(put("/api/v1/profils/999999/mensurations").contentType(MediaType.APPLICATION_JSON)
                .content(MES_MENSURATIONS))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Profil introuvable"));
    }

    @Test
    void unTypeInconnuOuUnKHorsBornesDonne400() throws Exception {
        mockMvc.perform(put("/api/v1/profils/{id}/mensurations", moi).contentType(MediaType.APPLICATION_JSON)
                .content(MES_MENSURATIONS));

        mockMvc.perform(get("/api/v1/vetements").param("type", "CHAPEAU")).andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/profils/{id}/vetements-compatibles", moi).param("k", "0"))
                .andExpect(status().isBadRequest());
    }
}
