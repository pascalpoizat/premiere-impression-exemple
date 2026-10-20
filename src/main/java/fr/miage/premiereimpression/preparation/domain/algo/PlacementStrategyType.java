package fr.miage.premiereimpression.preparation.domain.algo;

import java.util.EnumMap;
import java.util.Map;

/** Les stratégies proposées au candidat, chacune associée à une instance unique de sa classe. */
public enum PlacementStrategyType {
    CLOSE_TO_ACHIEVEMENT,
    MINIMAL_KNOWLEDGE;

    private static final Map<PlacementStrategyType, PlacementStrategy> REGISTRE = new EnumMap<>(Map.of(
            CLOSE_TO_ACHIEVEMENT, new CloseToAchievement(),
            MINIMAL_KNOWLEDGE, new MinimalKnowledge()));

    public PlacementStrategy strategie() {
        return REGISTRE.get(this);
    }
}
