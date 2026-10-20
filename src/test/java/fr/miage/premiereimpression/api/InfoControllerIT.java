package fr.miage.premiereimpression.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/** Test d'intégration : contexte complet (H2 + Flyway) et appel HTTP simulé. */
@SpringBootTest
@AutoConfigureMockMvc
class InfoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getInfoReturnsProjectName() throws Exception {
        mockMvc.perform(get("/api/v1/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("premiere-impression-back"))
                .andExpect(jsonPath("$.version").isNotEmpty());
    }
}
