package fr.miage.premiereimpression.preparation.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.miage.premiereimpression.preparation.application.port.in.AjouterConceptRequisCommande;
import fr.miage.premiereimpression.preparation.application.port.in.PlanningCalcule;
import fr.miage.premiereimpression.preparation.domain.algo.PlacementStrategyType;
import fr.miage.premiereimpression.preparation.domain.exception.CandidatureInconnueException;
import fr.miage.premiereimpression.preparation.domain.exception.ConceptDejaSaisiException;
import fr.miage.premiereimpression.preparation.domain.exception.ConceptRequisInconnuException;
import fr.miage.premiereimpression.preparation.domain.exception.DonneeInvalideException;
import fr.miage.premiereimpression.preparation.domain.exception.EntretienSansDateException;
import fr.miage.premiereimpression.preparation.domain.model.Bloc;
import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequis;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequisId;

/** Services du module preparation, branchés sur des faux adaptateurs en mémoire et une horloge fixe. */
class ServicesPreparationTest {

    private static final LocalDate AUJOURD_HUI = LocalDate.of(2026, 10, 20);
    private static final CandidatureId DANS_TROIS_JOURS = new CandidatureId(1);
    private static final CandidatureId AUJOURD_HUI_MEME = new CandidatureId(2);
    private static final CandidatureId PASSEE = new CandidatureId(3);
    private static final CandidatureId SANS_DATE = new CandidatureId(4);
    private static final CandidatureId INCONNUE = new CandidatureId(99);

    private DepotsEnMemoire.Concepts concepts;
    private GestionConceptsRequisService gestion;
    private PlanningRevisionService planning;

    @BeforeEach
    void services() {
        concepts = new DepotsEnMemoire.Concepts();
        DepotsEnMemoire.Candidatures candidatures = new DepotsEnMemoire.Candidatures()
                .avec(DANS_TROIS_JOURS, AUJOURD_HUI.plusDays(3))
                .avec(AUJOURD_HUI_MEME, AUJOURD_HUI)
                .avec(PASSEE, AUJOURD_HUI.minusDays(5))
                .avec(SANS_DATE, null);
        Clock horloge = Clock.fixed(AUJOURD_HUI.atStartOfDay(ZoneId.of("Europe/Paris")).toInstant(),
                ZoneId.of("Europe/Paris"));
        gestion = new GestionConceptsRequisService(concepts, candidatures);
        planning = new PlanningRevisionService(concepts, candidatures, horloge);
    }

    private ConceptRequis ajouter(CandidatureId candidature, String nom, int actuel, int requis, int qtn) {
        return gestion.ajouter(new AjouterConceptRequisCommande(candidature, nom, actuel, requis, qtn));
    }

    @Test
    void leRangSuitLOrdreDeSaisie() {
        ajouter(DANS_TROIS_JOURS, "SQL", 1, 4, 60);
        ajouter(DANS_TROIS_JOURS, "Java", 3, 5, 240);

        assertThat(gestion.lister(DANS_TROIS_JOURS)).extracting(ConceptRequis::nom, ConceptRequis::rang)
                .containsExactly(org.assertj.core.groups.Tuple.tuple("SQL", 1),
                        org.assertj.core.groups.Tuple.tuple("Java", 2));
    }

    @Test
    void unNomDejaSaisiPourLaCandidatureEstUnConflit() {
        ajouter(DANS_TROIS_JOURS, "SQL", 1, 4, 60);

        assertThatThrownBy(() -> ajouter(DANS_TROIS_JOURS, " SQL ", 0, 2, 30))
                .isInstanceOf(ConceptDejaSaisiException.class);
        ajouter(AUJOURD_HUI_MEME, "SQL", 1, 4, 60);
    }

    @Test
    void uneCandidatureInconnueEstRefusee() {
        assertThatThrownBy(() -> ajouter(INCONNUE, "SQL", 1, 4, 60)).isInstanceOf(CandidatureInconnueException.class);
        assertThatThrownBy(() -> gestion.lister(INCONNUE)).isInstanceOf(CandidatureInconnueException.class);
        assertThatThrownBy(() -> planning.planifier(INCONNUE, PlacementStrategyType.CLOSE_TO_ACHIEVEMENT, List.of()))
                .isInstanceOf(CandidatureInconnueException.class);
    }

    @Test
    void supprimeUnConceptOuSignaleQuIlEstInconnu() {
        ConceptRequis sql = ajouter(DANS_TROIS_JOURS, "SQL", 1, 4, 60);

        gestion.supprimer(DANS_TROIS_JOURS, sql.id());

        assertThat(gestion.lister(DANS_TROIS_JOURS)).isEmpty();
        assertThatThrownBy(() -> gestion.supprimer(DANS_TROIS_JOURS, sql.id()))
                .isInstanceOf(ConceptRequisInconnuException.class);
        assertThatThrownBy(() -> gestion.supprimer(DANS_TROIS_JOURS, new ConceptRequisId(42)))
                .isInstanceOf(ConceptRequisInconnuException.class);
    }

    @Test
    void planifieSurLesJoursQuiPrecedentLEntretien() {
        ajouter(DANS_TROIS_JOURS, "SQL", 1, 3, 60);

        ajouter(DANS_TROIS_JOURS, "Docker", 2, 2, 30);

        PlanningCalcule calcule = planning.planifier(DANS_TROIS_JOURS, PlacementStrategyType.MINIMAL_KNOWLEDGE,
                List.of(30, 60, 60));

        assertThat(calcule.jours()).extracting(PlanningCalcule.Jour::date).containsExactly(AUJOURD_HUI,
                AUJOURD_HUI.plusDays(1), AUJOURD_HUI.plusDays(2));
        assertThat(calcule.jours().stream().flatMap(j -> j.blocs().stream()).toList())
                .extracting(Bloc::jour, Bloc::duree).containsExactly(
                        org.assertj.core.groups.Tuple.tuple(0, 30), org.assertj.core.groups.Tuple.tuple(1, 30),
                        org.assertj.core.groups.Tuple.tuple(1, 30), org.assertj.core.groups.Tuple.tuple(2, 30));
        assertThat(calcule.niveauxAtteints()).extracting(n -> n.concept().nom(), PlanningCalcule.NiveauAtteint::atteint)
                .containsExactly(org.assertj.core.groups.Tuple.tuple("SQL", 3),
                        org.assertj.core.groups.Tuple.tuple("Docker", 2));
        assertThat(calcule.tempsDisponible()).isEqualTo(150);
        assertThat(calcule.tempsUtilise()).isEqualTo(120);
        assertThat(calcule.tempsInutilise()).isEqualTo(30);
    }

    @Test
    void ilFautExactementUneDisponibiliteParJourJusquALaVeille() {
        assertThatThrownBy(() -> planning.planifier(DANS_TROIS_JOURS, PlacementStrategyType.CLOSE_TO_ACHIEVEMENT,
                List.of(60, 60))).isInstanceOf(DonneeInvalideException.class);
        assertThatThrownBy(() -> planning.planifier(DANS_TROIS_JOURS, PlacementStrategyType.CLOSE_TO_ACHIEVEMENT,
                null)).isInstanceOf(DonneeInvalideException.class);
    }

    @Test
    void caVide1EntretienAujourdhuiOuPasseDonneUnPlanningVide() {
        ajouter(AUJOURD_HUI_MEME, "SQL", 1, 4, 60);
        ajouter(PASSEE, "SQL", 1, 4, 60);

        assertThat(planning.planifier(AUJOURD_HUI_MEME, PlacementStrategyType.CLOSE_TO_ACHIEVEMENT, List.of()).jours())
                .isEmpty();
        assertThat(planning.planifier(PASSEE, PlacementStrategyType.CLOSE_TO_ACHIEVEMENT, List.of()).tempsUtilise())
                .isZero();
    }

    @Test
    void sansDateDEntretienOnNePeutPasPlanifier() {
        assertThatThrownBy(() -> planning.planifier(SANS_DATE, PlacementStrategyType.CLOSE_TO_ACHIEVEMENT, List.of()))
                .isInstanceOf(EntretienSansDateException.class);
    }

    @Test
    void laStrategieEstObligatoire() {
        assertThatThrownBy(() -> planning.planifier(DANS_TROIS_JOURS, null, List.of(60, 60, 60)))
                .isInstanceOf(DonneeInvalideException.class);
    }
}
