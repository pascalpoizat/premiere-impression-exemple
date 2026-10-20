package fr.miage.premiereimpression.candidature.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import fr.miage.premiereimpression.candidature.application.port.in.CreerProfilCommande;
import fr.miage.premiereimpression.candidature.domain.exception.DonneeInvalideException;
import fr.miage.premiereimpression.candidature.domain.exception.ProfilIntrouvableException;
import fr.miage.premiereimpression.candidature.domain.exception.PseudoDejaPrisException;
import fr.miage.premiereimpression.candidature.domain.model.Profil;
import fr.miage.premiereimpression.candidature.domain.model.ProfilId;

class GestionProfilServiceTest {

    private final DepotsEnMemoire.Profils profils = new DepotsEnMemoire.Profils();
    private final GestionProfilService service = new GestionProfilService(profils);

    @Test
    void creeEtRetrouveUnProfil() {
        Profil profil = service.creerProfil(new CreerProfilCommande("alex", "Java, SQL"));

        assertThat(service.trouverProfil(profil.id())).isEqualTo(profil);
        assertThat(service.existe(profil.id())).isTrue();
        assertThat(service.listerProfils()).containsExactly(profil);
    }

    @Test
    void refuseUnPseudoDejaPris() {
        service.creerProfil(new CreerProfilCommande("alex", ""));
        CreerProfilCommande doublon = new CreerProfilCommande(" alex ", "");

        assertThatThrownBy(() -> service.creerProfil(doublon)).isInstanceOf(PseudoDejaPrisException.class);
        assertThat(profils.tous()).hasSize(1);
    }

    @Test
    void nEnregistreRienSiLeProfilEstInvalide() {
        CreerProfilCommande invalide = new CreerProfilCommande("", "Java");

        assertThatThrownBy(() -> service.creerProfil(invalide)).isInstanceOf(DonneeInvalideException.class);
        assertThat(profils.tous()).isEmpty();
    }

    @Test
    void signaleUnProfilIntrouvable() {
        ProfilId inconnu = new ProfilId(42);

        assertThatThrownBy(() -> service.trouverProfil(inconnu)).isInstanceOf(ProfilIntrouvableException.class);
        assertThat(service.existe(inconnu)).isFalse();
    }
}
