package fr.miage.premiereimpression.interfaceweb;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;

/** F3 de bout en bout avec Playwright : les assertions attendent d'elles-mêmes que le front ait réagi. */
class RevisionPlaywrightIT extends ApplicationLancee {

    /** Assez de jours pour couvrir au moins deux semaines complètes, quel que soit le jour du lancement. */
    private static final int JOURS_SAISIE_RAPIDE = 15;

    private static final Pattern DUREE = Pattern.compile("\\((?:(\\d+) h (\\d+)|(\\d+) min)\\)");

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
        Page page = ouvrirLaRevision(pseudo);

        assertThat(page.locator("#calendrier [data-date]")).hasCount(ScenarioE4.DISPONIBILITES.size());
        for (int j = 0; j < ScenarioE4.DISPONIBILITES.size(); j++) {
            modifierLeJour(page, LocalDate.now().plusDays(j), ScenarioE4.DISPONIBILITES.get(j));
        }

        verifier(page, "CLOSE_TO_ACHIEVEMENT", ScenarioE4.CTA);
        verifier(page, "MINIMAL_KNOWLEDGE", ScenarioE4.MKW);
    }

    @Test
    void initialementLeTempsPlanifieDeChaqueJourVautZero() throws Exception {
        Page page = ouvrirLaRevision(profilAvecE4(ScenarioE4.JOURS_AVANT_ENTRETIEN));

        for (int j = 0; j < ScenarioE4.JOURS_AVANT_ENTRETIEN; j++) {
            assertThat(caseDuJour(page, LocalDate.now().plusDays(j)).locator(".revision")).hasText("0");
        }
    }

    @Test
    void apresPlanificationLeTempsPlanifieDeChaqueJourEstLaSommeDesRevisionsEtNeDepassePasSonBudget() throws Exception {
        Page page = ouvrirLaRevision(profilAvecE4(ScenarioE4.JOURS_AVANT_ENTRETIEN));
        for (int j = 0; j < ScenarioE4.DISPONIBILITES.size(); j++) {
            modifierLeJour(page, LocalDate.now().plusDays(j), ScenarioE4.DISPONIBILITES.get(j));
        }

        verifier(page, "CLOSE_TO_ACHIEVEMENT", ScenarioE4.CTA);

        int total = 0;
        for (int j = 0; j < ScenarioE4.JOURS_AVANT_ENTRETIEN; j++) {
            LocalDate jour = LocalDate.now().plusDays(j);
            int somme = sommeDesRevisionsDuPlanning(page, jour);
            assertThat(caseDuJour(page, jour).locator(".revision")).hasText(String.valueOf(somme));
            int budget = Integer.parseInt(caseDuJour(page, jour).locator(".volume").textContent().strip());
            assertTrue(somme <= budget, jour + " : " + somme + " min planifiées pour un budget de " + budget);
            total += somme;
        }
        // sans ce total, un planning dont on ne saurait pas lire les jours donnerait 0 partout et le test passerait à vide
        assertEquals(ScenarioE4.MINUTES_UTILISEES, total);
    }

    @Test
    void modifierLeBudgetApresPlanificationRemetLeTempsPlanifieAZero() throws Exception {
        Page page = ouvrirLaRevision(profilAvecE4(ScenarioE4.JOURS_AVANT_ENTRETIEN));
        for (int j = 0; j < ScenarioE4.DISPONIBILITES.size(); j++) {
            modifierLeJour(page, LocalDate.now().plusDays(j), ScenarioE4.DISPONIBILITES.get(j));
        }
        verifier(page, "CLOSE_TO_ACHIEVEMENT", ScenarioE4.CTA);
        assertThat(caseDuJour(page, LocalDate.now()).locator(".revision")).not().hasText("0");

        appliquer(page, "Tous", "45");

        for (int j = 0; j < ScenarioE4.JOURS_AVANT_ENTRETIEN; j++) {
            assertThat(caseDuJour(page, LocalDate.now().plusDays(j)).locator(".revision")).hasText("0");
        }
        assertThat(page.locator("#planning tr[data-date]")).hasCount(0);
    }

    /** Somme des durées des révisions du jour dans le planning détaillé (« (1 h 30) », « (45 min) ») ; 0 si le jour n'y figure pas. */
    private static int sommeDesRevisionsDuPlanning(Page page, LocalDate jour) {
        Locator ligne = page.locator("#planning tr[data-date=\"%s\"]".formatted(jour));
        if (ligne.count() == 0) {
            return 0;
        }
        Matcher duree = DUREE.matcher(ligne.textContent());
        int somme = 0;
        while (duree.find()) {
            somme += duree.group(1) != null
                    ? 60 * Integer.parseInt(duree.group(1)) + Integer.parseInt(duree.group(2))
                    : Integer.parseInt(duree.group(3));
        }
        return somme;
    }

    @Test
    void saisieRapideTrenteEnSemaineQuatreVingtDixLeSamediZeroLeDimanche() throws Exception {
        Page page = ouvrirLaRevision(profilPourLaSaisieRapide());
        verifierLeCalendrier(page, jour -> "0");

        saisirLExemple(page);

        verifierLeCalendrier(page, RevisionPlaywrightIT::volumeDeLExemple);
    }

    @Test
    void reinitialiserNeRemetAZeroQueLesJoursSelectionnes() throws Exception {
        Page page = ouvrirLaRevision(profilPourLaSaisieRapide());
        saisirLExemple(page);
        verifierLeCalendrier(page, RevisionPlaywrightIT::volumeDeLExemple);

        raccourci(page, "Samedi");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Réinitialiser").setExact(true)).click();

        verifierLeCalendrier(page, jour -> jour.getDayOfWeek() == DayOfWeek.SATURDAY ? "0" : volumeDeLExemple(jour));
    }

    private String profilPourLaSaisieRapide() throws Exception {
        String pseudo = "rapide-" + System.nanoTime() % 1_000_000;
        candidatureAvecEntretienDans(pseudo, JOURS_SAISIE_RAPIDE);
        return pseudo;
    }

    /** L'exemple de l'issue #11 : 30 min du lundi au vendredi, 90 le samedi, 0 le dimanche, en trois applications. */
    private static void saisirLExemple(Page page) {
        appliquer(page, "Semaine", "30");
        appliquer(page, "Samedi", "90");
        appliquer(page, "Dimanche", "0");
    }

    private static String volumeDeLExemple(LocalDate jour) {
        return switch (jour.getDayOfWeek()) {
            case SATURDAY -> "90";
            case SUNDAY -> "0";
            default -> "30";
        };
    }

    private static Page ouvrirLaRevision(String pseudo) {
        Page page = navigateur.newPage();
        page.navigate(FRONT);
        page.selectOption("#choix-profil", new SelectOption().setLabel(pseudo));
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Révision")).click();
        assertThat(page.locator("#revision-candidature")).containsText(pseudo);
        return page;
    }

    private static Locator caseDuJour(Page page, LocalDate jour) {
        return page.locator("#calendrier [data-date=\"%s\"]".formatted(jour));
    }

    private static void modifierLeJour(Page page, LocalDate jour, String minutes) {
        caseDuJour(page, jour).click();
        Locator champ = caseDuJour(page, jour).locator("input");
        champ.fill(minutes);
        champ.press("Enter");
        assertThat(caseDuJour(page, jour).locator(".volume")).hasText(minutes);
    }

    private static void raccourci(Page page, String nom) {
        page.locator("#raccourcis-jours").getByRole(AriaRole.BUTTON,
                new Locator.GetByRoleOptions().setName(nom).setExact(true)).click();
    }

    private static void appliquer(Page page, String nomDuRaccourci, String minutes) {
        raccourci(page, nomDuRaccourci);
        page.locator("#volume-jours").fill(minutes);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Appliquer").setExact(true)).click();
    }

    private static void verifierLeCalendrier(Page page, Function<LocalDate, String> attendu) {
        for (int j = 0; j < JOURS_SAISIE_RAPIDE; j++) {
            LocalDate jour = LocalDate.now().plusDays(j);
            assertThat(caseDuJour(page, jour).locator(".volume")).hasText(attendu.apply(jour));
        }
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
