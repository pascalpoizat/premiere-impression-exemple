package fr.miage.premiereimpression.tenue.domain.algo;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import fr.miage.premiereimpression.tenue.domain.exception.DonneeInvalideException;
import fr.miage.premiereimpression.tenue.domain.model.Ajustement;
import fr.miage.premiereimpression.tenue.domain.model.Mensurations;
import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;
import fr.miage.premiereimpression.tenue.domain.model.Vetement;

/** Les k vêtements des autres profils qui vont le mieux à une personne. */
public final class RechercheVetementsCompatibles {

    public static final int K_MAX = 50;

    /** Score décroissant, puis identifiant croissant : ordre total, donc résultat déterministe. */
    static final Comparator<Ajustement> DU_MEILLEUR_AU_MOINS_BON = Comparator
            .comparingDouble(Ajustement::score).reversed()
            .thenComparingLong(ajustement -> ajustement.vetement().id().valeur());

    private final EvaluateurAjustement evaluateur = new EvaluateurAjustement();

    public List<Ajustement> rechercher(Mensurations mensurations, Collection<Vetement> vetements, int k,
            Optional<TypeVetement> type) {
        if (k < 1 || k > K_MAX) {
            throw new DonneeInvalideException("Le nombre de vêtements demandés doit être compris entre 1 et " + K_MAX);
        }
        List<Ajustement> candidats = vetements.stream()
                .filter(vetement -> !vetement.proprietaire().equals(mensurations.profil()))
                .filter(vetement -> type.map(vetement.type()::equals).orElse(true))
                .map(vetement -> evaluateur.evaluer(mensurations, vetement))
                .flatMap(Optional::stream)
                .toList();
        return MeilleursK.parmi(candidats, k, DU_MEILLEUR_AU_MOINS_BON);
    }
}
