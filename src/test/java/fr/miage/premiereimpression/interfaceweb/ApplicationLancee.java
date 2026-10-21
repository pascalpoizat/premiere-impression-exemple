package fr.miage.premiereimpression.interfaceweb;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;

/**
 * Socle des tests d'interface : le back tourne sur le port 8080 et le front est servi sur le port 8000,
 * comme en développement (le front appelle {@code localhost:8080}, et le CORS n'autorise que le port 8000).
 * Ces ports doivent être libres. Lancement : {@code ./gradlew testInterface} (hors de {@code ./gradlew build}).
 * <p>
 * Les données sont créées par le test via l'API : un entretien daté par rapport à aujourd'hui, pour que le test
 * ne dépende pas de la date du jeu de démonstration.
 */
@Tag("interface")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "server.port=8080")
abstract class ApplicationLancee {

    static final String FRONT = "http://localhost:8000";
    private static final String API = "http://localhost:8080/api/v1";
    private static final Pattern ID = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");

    private static HttpServer serveurFront;
    private final HttpClient http = HttpClient.newHttpClient();

    @BeforeAll
    static void servirLeFront() {
        serveurFront = SimpleFileServer.createFileServer(new InetSocketAddress(8000), Path.of("front").toAbsolutePath(),
                SimpleFileServer.OutputLevel.NONE);
        serveurFront.start();
    }

    @AfterAll
    static void arreterLeFront() {
        serveurFront.stop(0);
    }

    long creer(String chemin, String json) throws IOException, InterruptedException {
        HttpResponse<String> reponse = http.send(HttpRequest.newBuilder(URI.create(API + chemin))
                .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build(),
                HttpResponse.BodyHandlers.ofString());
        Matcher id = ID.matcher(reponse.body());
        if (reponse.statusCode() != 201 || !id.find()) {
            throw new IllegalStateException(chemin + " : " + reponse.statusCode() + " " + reponse.body());
        }
        return Long.parseLong(id.group(1));
    }

    /** Un profil {@code pseudo} et sa candidature (entreprise {@code pseudo}), entretien dans {@code jours} jours ; renvoie son id. */
    long candidatureAvecEntretienDans(String pseudo, int jours) throws IOException, InterruptedException {
        long profil = creer("/profils", "{\"pseudo\": \"%s\"}".formatted(pseudo));
        return creer("/candidatures", """
                {"profilId": %d, "entreprise": "%s", "poste": "Développeur", "texteAnnonce": "Annonce",
                 "dateHeureEntretien": "%sT10:00:00"}""".formatted(profil, pseudo, LocalDate.now().plusDays(jours)));
    }

    /** Un profil avec une seule candidature, dont l'annonce est {@code annonce} ; l'entreprise porte le pseudo. */
    String profilAvecAnnonce(String annonce) throws IOException, InterruptedException {
        String pseudo = "annonce-" + System.nanoTime() % 1_000_000;
        long profil = creer("/profils", "{\"pseudo\": \"%s\"}".formatted(pseudo));
        creer("/candidatures", """
                {"profilId": %d, "entreprise": "%s", "poste": "Développeur", "texteAnnonce": "%s"}"""
                .formatted(profil, pseudo, annonce));
        return pseudo;
    }

    /**
     * Un profil dont la candidature a son entretien dans {@code jours} jours, avec les concepts de l'exemple E4.
     * Le pseudo sert aussi de nom d'entreprise : le test peut attendre que le front affiche CETTE candidature.
     */
    String profilAvecE4(int jours) throws IOException, InterruptedException {
        String pseudo = "e4-" + System.nanoTime() % 1_000_000;
        long candidature = candidatureAvecEntretienDans(pseudo, jours);
        String concepts = "/candidatures/%d/concepts-requis".formatted(candidature);
        creer(concepts, "{\"nom\": \"C1\", \"actuel\": 3, \"requis\": 5, \"qtn\": 240}");
        creer(concepts, "{\"nom\": \"C2\", \"actuel\": 1, \"requis\": 4, \"qtn\": 60}");
        creer(concepts, "{\"nom\": \"C3\", \"actuel\": 0, \"requis\": 4, \"qtn\": 120}");
        return pseudo;
    }
}
