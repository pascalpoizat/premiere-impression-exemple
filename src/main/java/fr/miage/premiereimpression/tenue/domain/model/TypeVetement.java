package fr.miage.premiereimpression.tenue.domain.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.miage.premiereimpression.tenue.domain.model.Mesure.CARRURE;
import static fr.miage.premiereimpression.tenue.domain.model.Mesure.LONGUEUR_JAMBE;
import static fr.miage.premiereimpression.tenue.domain.model.Mesure.TOUR_HANCHES;
import static fr.miage.premiereimpression.tenue.domain.model.Mesure.TOUR_POITRINE;
import static fr.miage.premiereimpression.tenue.domain.model.Mesure.TOUR_TAILLE;

/** Types de vêtements et, pour chacun, les critères d'ajustement (voir la fiche F2). */
public enum TypeVetement {
    VESTE(List.of(
            new Critere(CARRURE, 3, 1, 1, 3),
            new Critere(TOUR_POITRINE, 2, 10, 4, 8),
            new Critere(TOUR_TAILLE, 1, 10, 6, 10))),
    CHEMISE(List.of(
            new Critere(CARRURE, 2, 1, 1, 3),
            new Critere(TOUR_POITRINE, 3, 8, 4, 8),
            new Critere(TOUR_TAILLE, 1, 8, 6, 10))),
    PANTALON(List.of(
            new Critere(TOUR_TAILLE, 3, 1, 1, 4),
            new Critere(TOUR_HANCHES, 2, 4, 3, 8),
            new Critere(LONGUEUR_JAMBE, 2, 0, 2, 4))),
    JUPE(List.of(
            new Critere(TOUR_TAILLE, 3, 1, 1, 4),
            new Critere(TOUR_HANCHES, 2, 4, 3, 8))),
    ROBE(List.of(
            new Critere(TOUR_POITRINE, 2, 6, 3, 8),
            new Critere(TOUR_TAILLE, 2, 4, 3, 8),
            new Critere(TOUR_HANCHES, 2, 4, 3, 8)));

    private final List<Critere> criteres;

    TypeVetement(List<Critere> criteres) {
        this.criteres = criteres;
    }

    public List<Critere> criteres() {
        return criteres;
    }

    public Set<Mesure> mesuresRequises() {
        return criteres.stream().map(Critere::mesure).collect(Collectors.toUnmodifiableSet());
    }
}
