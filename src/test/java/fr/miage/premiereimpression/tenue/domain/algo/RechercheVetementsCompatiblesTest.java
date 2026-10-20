package fr.miage.premiereimpression.tenue.domain.algo;

import static fr.miage.premiereimpression.tenue.domain.DonneesTenue.MOI;
import static fr.miage.premiereimpression.tenue.domain.DonneesTenue.PRETEUR;
import static fr.miage.premiereimpression.tenue.domain.DonneesTenue.vetement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import fr.miage.premiereimpression.tenue.domain.DonneesTenue;
import fr.miage.premiereimpression.tenue.domain.exception.DonneeInvalideException;
import fr.miage.premiereimpression.tenue.domain.model.Ajustement;
import fr.miage.premiereimpression.tenue.domain.model.Mensurations;
import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;
import fr.miage.premiereimpression.tenue.domain.model.Vetement;

class RechercheVetementsCompatiblesTest {

    private final RechercheVetementsCompatibles recherche = new RechercheVetementsCompatibles();
    private final Mensurations moi = DonneesTenue.moi();

    private List<Long> ids(List<Ajustement> ajustements) {
        return ajustements.stream().map(a -> a.vetement().id().valeur()).toList();
    }

    @Test
    void classeDuMeilleurAuMoinsBonEtEcarteLesTropPetits() {
        List<Vetement> vetements = List.of(
                vetement(1, PRETEUR, TypeVetement.VESTE, moi, 6),
                vetement(2, PRETEUR, TypeVetement.VESTE, moi, 0),
                vetement(3, PRETEUR, TypeVetement.VESTE, moi, -10),
                vetement(4, PRETEUR, TypeVetement.VESTE, moi, 2));

        assertThat(ids(recherche.rechercher(moi, vetements, 10, Optional.empty()))).containsExactly(2L, 4L, 1L);
    }

    @Test
    void neProposeJamaisSesPropresVetements() {
        List<Vetement> vetements = List.of(
                vetement(1, MOI, TypeVetement.VESTE, moi, 0),
                vetement(2, PRETEUR, TypeVetement.VESTE, moi, 3));

        assertThat(ids(recherche.rechercher(moi, vetements, 10, Optional.empty()))).containsExactly(2L);
    }

    @Test
    void filtreParType() {
        List<Vetement> vetements = List.of(
                vetement(1, PRETEUR, TypeVetement.VESTE, moi, 0),
                vetement(2, PRETEUR, TypeVetement.PANTALON, moi, 0));

        assertThat(ids(recherche.rechercher(moi, vetements, 10, Optional.of(TypeVetement.PANTALON))))
                .containsExactly(2L);
    }

    @Test
    void lesEgalitesSontDepartageesParIdentifiant() {
        List<Vetement> vetements = List.of(
                vetement(7, PRETEUR, TypeVetement.JUPE, moi, 0),
                vetement(3, PRETEUR, TypeVetement.JUPE, moi, 0));

        assertThat(ids(recherche.rechercher(moi, vetements, 10, Optional.empty()))).containsExactly(3L, 7L);
    }

    @Test
    void limiteLeResultatAK() {
        List<Vetement> vetements = List.of(
                vetement(1, PRETEUR, TypeVetement.ROBE, moi, 1),
                vetement(2, PRETEUR, TypeVetement.ROBE, moi, 2),
                vetement(3, PRETEUR, TypeVetement.ROBE, moi, 3));

        assertThat(ids(recherche.rechercher(moi, vetements, 2, Optional.empty()))).containsExactly(1L, 2L);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, RechercheVetementsCompatibles.K_MAX + 1 })
    void refuseUnKHorsBornes(int k) {
        List<Vetement> aucun = List.of();
        assertThatThrownBy(() -> recherche.rechercher(moi, aucun, k, Optional.empty()))
                .isInstanceOf(DonneeInvalideException.class);
    }
}
