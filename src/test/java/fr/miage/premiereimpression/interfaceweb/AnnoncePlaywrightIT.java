package fr.miage.premiereimpression.interfaceweb;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;

/**
 * F1, affichage de l'annonce : un test par trace de l'automate du bouton, depuis l'état initial,
 * chaque trace s'arrêtant quand elle boucle.
 * <pre>
 * -&gt;(0)
 * (0) --Afficher--&gt; (1)
 * (1) --Masquer--&gt;  (0)
 * </pre>
 * État 0 : texte masqué, bouton « Afficher ». État 1 : texte affiché, bouton « Masquer ».
 */
class AnnoncePlaywrightIT extends ApplicationLancee {

    private static final String ANNONCE = "Nous recherchons un développeur Java pour notre plateforme de paiement.";

    private static Playwright playwright;
    private static Browser navigateur;
    private Page page;

    @BeforeAll
    static void lancerLeNavigateur() {
        playwright = Playwright.create();
        navigateur = playwright.chromium().launch();
    }

    @AfterAll
    static void fermerLeNavigateur() {
        playwright.close();
    }

    /** Amène l'interface dans l'état initial de l'automate : l'analyse de la candidature est affichée. */
    @BeforeEach
    void ouvrirLAnalyseDeLaCandidature() throws Exception {
        String pseudo = profilAvecAnnonce(ANNONCE);
        page = navigateur.newPage();
        page.navigate(FRONT + "/#candidatures");
        page.selectOption("#choix-profil", new SelectOption().setLabel(pseudo));
        page.locator("#liste-candidatures li", new Page.LocatorOptions().setHasText(pseudo)).click();
        assertThat(page.locator("#analyse h3")).containsText(pseudo);
    }

    @AfterEach
    void fermerLaPage() {
        page.close();
    }

    @Test
    void initialementLeTexteEstMasqueEtLeBoutonAfficheAfficher() {
        etatMasque();
    }

    @Test
    void depuisLEtatMasqueAfficherAfficheLeTexteEtLeBoutonAfficheMasquer() {
        etatMasque();
        bouton("Afficher").click();
        etatAffiche();
    }

    @Test
    void depuisLEtatAfficheMasquerMasqueLeTexteEtLeBoutonAfficheAfficher() {
        etatMasque();
        bouton("Afficher").click();
        etatAffiche();
        bouton("Masquer").click();
        etatMasque();
    }

    /** État 0 de l'automate. */
    private void etatMasque() {
        assertThat(texteDeLAnnonce()).isHidden();
        assertThat(bouton("Afficher")).isVisible();
        assertThat(bouton("Masquer")).hasCount(0);
    }

    /** État 1 de l'automate. */
    private void etatAffiche() {
        assertThat(texteDeLAnnonce()).isVisible();
        assertThat(bouton("Masquer")).isVisible();
        assertThat(bouton("Afficher")).hasCount(0);
    }

    private Locator texteDeLAnnonce() {
        return page.locator("#analyse").getByText(ANNONCE);
    }

    private Locator bouton(String libelle) {
        return page.locator("#analyse").getByRole(AriaRole.BUTTON,
                new Locator.GetByRoleOptions().setName(libelle).setExact(true));
    }
}
