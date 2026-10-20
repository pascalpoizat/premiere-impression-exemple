package fr.miage.premiereimpression.preparation.adapter.in.web;

import java.time.LocalDate;
import java.util.List;

import fr.miage.premiereimpression.preparation.application.port.in.PlanningCalcule;
import fr.miage.premiereimpression.preparation.domain.algo.PlacementStrategyType;
import fr.miage.premiereimpression.preparation.domain.model.Bloc;

/** Planning de révision : les jours avec leurs blocs, puis le niveau atteint pour chaque concept. */
record PlanningReponse(long candidatureId, PlacementStrategyType strategie, List<Jour> jours,
        List<NiveauAtteint> niveauxAtteints, long tempsDisponible, long tempsUtilise, long tempsInutilise) {

    record Jour(LocalDate date, int disponible, List<BlocReponse> blocs) {
    }

    record BlocReponse(String concept, int niveau, int duree) {

        static BlocReponse depuis(Bloc bloc) {
            return new BlocReponse(bloc.niveau().concept().nom(), bloc.niveau().k(), bloc.duree());
        }
    }

    record NiveauAtteint(String concept, int actuel, int atteint, int requis) {
    }

    static PlanningReponse depuis(long candidatureId, PlacementStrategyType strategie, PlanningCalcule planning) {
        List<Jour> jours = planning.jours().stream()
                .map(j -> new Jour(j.date(), j.disponible(), j.blocs().stream().map(BlocReponse::depuis).toList()))
                .toList();
        List<NiveauAtteint> niveaux = planning.niveauxAtteints().stream()
                .map(n -> new NiveauAtteint(n.concept().nom(), n.concept().actuel(), n.atteint(), n.concept().requis()))
                .toList();
        return new PlanningReponse(candidatureId, strategie, jours, niveaux, planning.tempsDisponible(),
                planning.tempsUtilise(), planning.tempsInutilise());
    }
}
