package fr.miage.premiereimpression.preparation.domain.algo;

import java.util.Comparator;

import fr.miage.premiereimpression.preparation.domain.model.ConceptRequis;

/**
 * Stratégie de placement (pattern Strategy) : elle dit quel concept monter d'un niveau en priorité.
 * Le concept le plus petit pour {@link #priorite()} passe d'abord ; les égalités sont départagées
 * par une règle commune à toutes les stratégies, appliquée par {@link PlanningDeRevision}.
 */
public interface PlacementStrategy {

    Comparator<ConceptRequis> priorite();
}
