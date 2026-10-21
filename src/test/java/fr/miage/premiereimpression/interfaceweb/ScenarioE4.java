package fr.miage.premiereimpression.interfaceweb;

import java.util.List;
import java.util.Map;

/**
 * Exemple E4 de la fiche F3, joué dans le front : 4 jours de révision totalisant d = 300 minutes,
 * niveaux atteints déroulés à la main par le responsable.
 */
final class ScenarioE4 {

    static final int JOURS_AVANT_ENTRETIEN = 4;
    static final List<String> DISPONIBILITES = List.of("120", "60", "120", "0");
    static final Map<String, String> CTA = Map.of("C1", "3", "C2", "4", "C3", "1");
    static final Map<String, String> MKW = Map.of("C1", "3", "C2", "2", "C3", "2");
    static final String BILAN = "Temps utilisé : 5 h 00 sur 5 h 00 disponibles.";
    static final int MINUTES_UTILISEES = 300;

    private ScenarioE4() {
    }
}
