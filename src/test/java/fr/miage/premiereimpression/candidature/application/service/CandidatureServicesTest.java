package fr.miage.premiereimpression.candidature.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.miage.premiereimpression.candidature.application.port.in.CreerCandidatureCommande;
import fr.miage.premiereimpression.candidature.application.port.in.CreerProfilCommande;
import fr.miage.premiereimpression.candidature.domain.exception.CandidatureIntrouvableException;
import fr.miage.premiereimpression.candidature.domain.exception.DonneeInvalideException;
import fr.miage.premiereimpression.candidature.domain.exception.ProfilIntrouvableException;
import fr.miage.premiereimpression.candidature.domain.model.AnalyseAnnonce;
import fr.miage.premiereimpression.candidature.domain.model.Candidature;
import fr.miage.premiereimpression.candidature.domain.model.CandidatureId;
import fr.miage.premiereimpression.candidature.domain.model.MotCle;
import fr.miage.premiereimpression.candidature.domain.model.Profil;
import fr.miage.premiereimpression.candidature.domain.model.ProfilId;

class CandidatureServicesTest {

    private final DepotsEnMemoire.Profils profils = new DepotsEnMemoire.Profils();
    private final DepotsEnMemoire.Candidatures candidatures = new DepotsEnMemoire.Candidatures();
    private final GestionCandidatureService gestion = new GestionCandidatureService(candidatures, profils);
    private final AnalyseAnnonceService analyse = new AnalyseAnnonceService(candidatures, profils);

    private Profil alex;

    @BeforeEach
    void unProfil() {
        alex = new GestionProfilService(profils).creerProfil(new CreerProfilCommande("alex", "Java, Spring"));
    }

    private Candidature candidater(String poste, String annonce) {
        return gestion.creerCandidature(new CreerCandidatureCommande(alex.id(), "ACME", poste, annonce,
                LocalDateTime.of(2026, 11, 5, 10, 0), "1 rue de la Paix, Paris"));
    }

    @Test
    void creeEtListeLesCandidaturesDUnProfil() {
        Candidature candidature = candidater("Développeur", "Java et Docker");

        assertThat(gestion.trouverCandidature(candidature.id())).isEqualTo(candidature);
        assertThat(gestion.listerCandidaturesDe(alex.id())).containsExactly(candidature);
        assertThat(gestion.listerCandidatures()).containsExactly(candidature);
    }

    @Test
    void refuseUneCandidaturePourUnProfilInconnu() {
        CreerCandidatureCommande commande = new CreerCandidatureCommande(new ProfilId(99), "ACME", "Dev",
                "Java", null, null);

        assertThatThrownBy(() -> gestion.creerCandidature(commande)).isInstanceOf(ProfilIntrouvableException.class);
        assertThat(candidatures.tous()).isEmpty();
    }

    @Test
    void signaleUneCandidatureIntrouvable() {
        CandidatureId inconnue = new CandidatureId(7);

        assertThatThrownBy(() -> gestion.trouverCandidature(inconnue))
                .isInstanceOf(CandidatureIntrouvableException.class);
        assertThatThrownBy(() -> analyse.analyser(inconnue, 10))
                .isInstanceOf(CandidatureIntrouvableException.class);
    }

    @Test
    void analyseLAnnonceAuRegardDuProfilQuiACandidate() {
        candidater("Data", "SQL Python Power BI");
        Candidature candidature = candidater("Développeur", "Java Spring Docker Kubernetes");

        AnalyseAnnonce resultat = analyse.analyser(candidature.id(), 10);

        assertThat(resultat.correspondance()).isPositive();
        assertThat(resultat.competencesATravailler()).extracting(MotCle::racine)
                .containsExactlyInAnyOrder("docker", "kubernet");
    }

    @Test
    void transmetLaValidationDeK() {
        Candidature candidature = candidater("Développeur", "Java");

        assertThatThrownBy(() -> analyse.analyser(candidature.id(), 0)).isInstanceOf(DonneeInvalideException.class);
    }

    @Test
    void listerLesCandidaturesDUnProfilInconnuEstUneErreur() {
        ProfilId inconnu = new ProfilId(99);
        assertThatThrownBy(() -> gestion.listerCandidaturesDe(inconnu)).isInstanceOf(ProfilIntrouvableException.class);
    }
}
