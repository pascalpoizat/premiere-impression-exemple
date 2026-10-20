package fr.miage.premiereimpression.interfaceweb;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Le même scénario avec Selenium, pour comparer (fiche F3) : ici, chaque attente est explicite.
 * Selenium Manager utilise le Chrome installé et télécharge le pilote correspondant.
 */
class RevisionSeleniumIT extends ApplicationLancee {

    private static WebDriver pilote;
    private static WebDriverWait attente;

    @BeforeAll
    static void lancerLeNavigateur() {
        pilote = new ChromeDriver(new ChromeOptions().addArguments("--headless=new", "--window-size=1300,2600"));
        attente = new WebDriverWait(pilote, Duration.ofSeconds(10));
    }

    @AfterAll
    static void fermerLeNavigateur() {
        pilote.quit();
    }

    @Test
    void planifierE4AvecLesDeuxStrategies() throws Exception {
        String pseudo = profilAvecE4(ScenarioE4.JOURS_AVANT_ENTRETIEN);
        pilote.get(FRONT);
        attente.until(ExpectedConditions.textToBePresentInElementLocated(By.id("choix-profil"), pseudo));
        new Select(pilote.findElement(By.id("choix-profil"))).selectByVisibleText(pseudo);

        // attentes explicites : le profil affiché au chargement a peut-être déjà ses propres champs ; il faut attendre
        // que la section montre CETTE candidature, sinon on garderait des éléments retirés de la page (StaleElement…)
        attente.until(ExpectedConditions.textToBePresentInElementLocated(By.id("revision-candidature"), pseudo));
        List<WebElement> jours = attente.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("#disponibilites input"), ScenarioE4.DISPONIBILITES.size()));
        for (int j = 0; j < jours.size(); j++) {
            jours.get(j).clear();
            jours.get(j).sendKeys(ScenarioE4.DISPONIBILITES.get(j));
        }

        verifier("CLOSE_TO_ACHIEVEMENT", ScenarioE4.CTA);
        verifier("MINIMAL_KNOWLEDGE", ScenarioE4.MKW);
    }

    private static void verifier(String strategie, Map<String, String> atteints) {
        new Select(pilote.findElement(By.id("strategie"))).selectByValue(strategie);
        pilote.findElement(By.cssSelector("#form-planning button[type=submit]")).click();
        // attendre le planning de CETTE stratégie : sinon on lirait l'ancien, encore affiché
        attente.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("#planning h3"), strategie));
        WebElement planning = pilote.findElement(By.id("planning"));
        assertThat(planning.getText()).contains(ScenarioE4.BILAN);
        List<WebElement> tables = planning.findElements(By.tagName("table"));
        List<WebElement> lignes = tables.get(tables.size() - 1).findElements(By.tagName("tr"));
        atteints.forEach((concept, niveau) -> {
            WebElement ligne = lignes.stream().filter(l -> l.getText().startsWith(concept + " ")).findFirst()
                    .orElseThrow();
            assertThat(ligne.findElements(By.tagName("td")).get(2).getText()).isEqualTo(niveau);
        });
    }
}
