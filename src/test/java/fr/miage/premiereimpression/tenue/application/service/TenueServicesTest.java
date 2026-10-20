package fr.miage.premiereimpression.tenue.application.service;

import static fr.miage.premiereimpression.tenue.domain.DonneesTenue.MOI;
import static fr.miage.premiereimpression.tenue.domain.DonneesTenue.PRETEUR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import fr.miage.premiereimpression.tenue.application.port.in.DeclarerVetementCommande;
import fr.miage.premiereimpression.tenue.domain.DonneesTenue;
import fr.miage.premiereimpression.tenue.domain.exception.DonneeInvalideException;
import fr.miage.premiereimpression.tenue.domain.exception.MensurationsIntrouvablesException;
import fr.miage.premiereimpression.tenue.domain.exception.ProfilInconnuException;
import fr.miage.premiereimpression.tenue.domain.model.Ajustement;
import fr.miage.premiereimpression.tenue.domain.model.Mensurations;
import fr.miage.premiereimpression.tenue.domain.model.ProfilId;
import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;
import fr.miage.premiereimpression.tenue.domain.model.Vetement;

class TenueServicesTest {

    private final DepotsEnMemoire.Profils profils = new DepotsEnMemoire.Profils().avec(MOI, PRETEUR);
    private final DepotsEnMemoire.MensurationsEnMemoire mensurations = new DepotsEnMemoire.MensurationsEnMemoire();
    private final DepotsEnMemoire.Vetements vetements = new DepotsEnMemoire.Vetements();
    private final GestionMensurationsService gestionMensurations = new GestionMensurationsService(mensurations, profils);
    private final GestionVetementsService gestionVetements = new GestionVetementsService(vetements, profils);
    private final RechercheVetementsService recherche = new RechercheVetementsService(mensurations, vetements, profils);

    private final Mensurations moi = DonneesTenue.moi();

    private Vetement declarer(ProfilId proprietaire, TypeVetement type, double decalage) {
        Vetement modele = DonneesTenue.vetement(1, proprietaire, type, moi, decalage);
        return gestionVetements.declarer(new DeclarerVetementCommande(proprietaire, type, modele.nom(), null,
                modele.mesures()));
    }

    @Test
    void enregistreEtRemplaceLesMensurations() {
        gestionMensurations.enregistrer(MOI, moi.valeurs());
        Mensurations nouvelles = DonneesTenue.corps(MOI, 100, 85, 100, 81, 45);
        gestionMensurations.enregistrer(MOI, nouvelles.valeurs());

        assertThat(gestionMensurations.trouver(MOI)).isEqualTo(nouvelles);
    }

    @Test
    void refuseLesMensurationsDUnProfilInconnu() {
        ProfilId inconnu = new ProfilId(99);
        Map<fr.miage.premiereimpression.tenue.domain.model.Mesure, Double> valeurs = moi.valeurs();

        assertThatThrownBy(() -> gestionMensurations.enregistrer(inconnu, valeurs))
                .isInstanceOf(ProfilInconnuException.class);
        assertThatThrownBy(() -> gestionMensurations.trouver(inconnu)).isInstanceOf(ProfilInconnuException.class);
    }

    @Test
    void signaleDesMensurationsAbsentes() {
        assertThatThrownBy(() -> gestionMensurations.trouver(MOI))
                .isInstanceOf(MensurationsIntrouvablesException.class);
        assertThatThrownBy(() -> recherche.compatibles(MOI, 5, Optional.empty()))
                .isInstanceOf(MensurationsIntrouvablesException.class);
    }

    @Test
    void declareEtListeLesVetementsParType() {
        Vetement veste = declarer(PRETEUR, TypeVetement.VESTE, 0);
        Vetement jupe = declarer(PRETEUR, TypeVetement.JUPE, 0);

        assertThat(gestionVetements.lister(Optional.empty())).containsExactly(veste, jupe);
        assertThat(gestionVetements.lister(Optional.of(TypeVetement.JUPE))).containsExactly(jupe);
    }

    @Test
    void refuseUnVetementDUnProprietaireInconnu() {
        ProfilId inconnu = new ProfilId(99);
        assertThatThrownBy(() -> declarer(inconnu, TypeVetement.VESTE, 0)).isInstanceOf(ProfilInconnuException.class);
        assertThat(vetements.tous()).isEmpty();
    }

    @Test
    void proposeLesVetementsDesAutresQuiVontLeMieux() {
        gestionMensurations.enregistrer(MOI, moi.valeurs());
        declarer(MOI, TypeVetement.VESTE, 0);
        Vetement ample = declarer(PRETEUR, TypeVetement.VESTE, 8);
        Vetement parfaite = declarer(PRETEUR, TypeVetement.VESTE, 0);
        declarer(PRETEUR, TypeVetement.VESTE, -20);

        assertThat(recherche.compatibles(MOI, 10, Optional.empty())).extracting(Ajustement::vetement)
                .containsExactly(parfaite, ample);
    }

    @Test
    void transmetLaValidationDeK() {
        gestionMensurations.enregistrer(MOI, moi.valeurs());
        assertThatThrownBy(() -> recherche.compatibles(MOI, 0, Optional.empty()))
                .isInstanceOf(DonneeInvalideException.class);
    }
}
