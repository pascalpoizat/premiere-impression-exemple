package fr.miage.premiereimpression.interfaceweb;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;

/** F3 de bout en bout avec Playwright : les assertions attendent d'elles-mêmes que le front ait réagi. */
class RevisionPlaywrightIT extends ApplicationLancee {

    private static Playwright playwright;
    private static Browser navigateur;

    @BeforeAll
    static void lancerLeNavigateur() {
        playwright = Playwright.create();
        navigateur = playwright.chromium().launch();
    }

    @AfterAll
    static void fermerLeNavigateur() {
        playwright.close();
    }

    @Test
    void planifierE4AvecLesDeuxStrategies() throws Exception {
        String pseudo = profilAvecE4(ScenarioE4.JOURS_AVANT_ENTRETIEN);
        Page page = navigateur.newPage();
        page.navigate(FRONT);
        page.selectOption("#choix-profil", new com.microsoft.playwright.options.SelectOption().setLabel(pseudo));
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Révision")).click();

        assertThat(page.locator("#revision-candidature")).containsText(pseudo);
        Locator jours = page.locator("#disponibilites input");
        assertThat(jours).hasCount(ScenarioE4.DISPONIBILITES.size());
        List<Locator> champs = jours.all();
        for (int j = 0; j < champs.size(); j++) {
            champs.get(j).fill(ScenarioE4.DISPONIBILITES.get(j));
        }

        verifier(page, "CLOSE_TO_ACHIEVEMENT", ScenarioE4.CTA);
        verifier(page, "MINIMAL_KNOWLEDGE", ScenarioE4.MKW);
    }

    private static void verifier(Page page, String strategie, Map<String, String> atteints) {
        page.selectOption("#strategie", strategie);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Planifier")).click();
        Locator planning = page.locator("#planning");
        assertThat(planning.locator("h3")).containsText(strategie);
        assertThat(planning).containsText(ScenarioE4.BILAN);
        Locator niveaux = planning.locator("table").last();
        atteints.forEach((concept, niveau) -> assertThat(niveaux.locator("tr",
                new Locator.LocatorOptions().setHasText(concept)).locator("td").nth(2)).hasText(niveau));
    }
}
