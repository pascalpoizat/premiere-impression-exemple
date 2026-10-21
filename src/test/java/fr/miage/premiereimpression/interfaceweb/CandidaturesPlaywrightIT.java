package fr.miage.premiereimpression.interfaceweb;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;

/** F1, liste des candidatures du profil courant (bug #12). */
class CandidaturesPlaywrightIT extends ApplicationLancee {

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
    void deuxChargementsDuMemeProfilQuiSeChevauchentNAffichentChaqueCandidatureQuUneFois() throws Exception {
        String pseudo = profilAvecAnnonce("Annonce");
        Page page = navigateur.newPage();
        page.navigate(FRONT);
        page.selectOption("#choix-profil", new SelectOption().setLabel(pseudo));
        Locator candidature = page.locator("#liste-candidatures li", new Page.LocatorOptions().setHasText(pseudo));
        assertThat(candidature).hasCount(1);

        // deux événements dans la même tâche JavaScript : le second part avant toute réponse au premier
        page.evaluate("""
                () => {
                  const choix = document.querySelector('#choix-profil');
                  choix.dispatchEvent(new Event('change'));
                  choix.dispatchEvent(new Event('change'));
                }""");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        assertThat(candidature).hasCount(1);
    }
}
