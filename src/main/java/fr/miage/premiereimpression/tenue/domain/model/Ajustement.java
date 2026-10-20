package fr.miage.premiereimpression.tenue.domain.model;

import java.util.List;

/**
 * Résultat de l'ajustement d'un vêtement sur une personne.
 *
 * @param score dans ]0, 1], 1 pour un ajustement parfait
 */
public record Ajustement(Vetement vetement, double score, List<DetailMesure> details) {

    public Ajustement {
        details = List.copyOf(details);
    }
}
