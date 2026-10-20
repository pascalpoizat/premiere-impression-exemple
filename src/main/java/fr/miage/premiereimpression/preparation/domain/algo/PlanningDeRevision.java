package fr.miage.premiereimpression.preparation.domain.algo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.stream.Stream;

import fr.miage.premiereimpression.preparation.domain.exception.DonneeInvalideException;
import fr.miage.premiereimpression.preparation.domain.model.Bloc;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequis;
import fr.miage.premiereimpression.preparation.domain.model.Niveau;

/**
 * Planning de révision jusqu'au jour J, en trois étapes testables séparément (fiche F3) :
 * <ol>
 * <li>{@link #sequence} : la stratégie ordonne la montée des niveaux, niveau entier par niveau entier ;</li>
 * <li>{@link #prefixe} : on garde le plus long préfixe qui tient dans le temps disponible ;</li>
 * <li>{@link #placement} : on remplit les jours dans l'ordre, un niveau pouvant s'étaler sur plusieurs jours.</li>
 * </ol>
 * Complexité : O((K + 1) log m) pour la séquence coupée ({@code K} niveaux gardés, {@code m} concepts),
 * au pire O(L log m) quand tout tient ({@code L} niveaux en tout) ; linéaire ensuite.
 */
public final class PlanningDeRevision {

    public static final int DISPONIBILITE_MAX_PAR_JOUR = 24 * 60;

    /** Règle d'égalité commune : niveau actuel le plus bas, puis niveau requis le plus haut, puis plus petit rang. */
    static final Comparator<ConceptRequis> EGALITES = Comparator.comparingInt(ConceptRequis::actuel)
            .thenComparing(Comparator.comparingInt(ConceptRequis::requis).reversed())
            .thenComparingInt(ConceptRequis::rang);

    private PlanningDeRevision() {
    }

    public static List<Bloc> planning(List<ConceptRequis> concepts, PlacementStrategy strategie,
            List<Integer> disponibilites) {
        verifierDisponibilites(disponibilites);
        long d = disponibilites.stream().mapToLong(Integer::longValue).sum();
        return placer(prefixe(sequence(concepts, strategie), d), disponibilites);
    }

    /**
     * placement(besoin) : la séquence des niveaux, comme si le temps était illimité. Le flux est paresseux :
     * un niveau n'est tiré de la file que lorsqu'on le demande, si bien que {@link #prefixe} ne calcule
     * que les niveaux gardés, plus celui qui fait dépasser. Il ne se parcourt qu'une fois.
     * La file contient l'état courant de chaque concept : seule la priorité du concept choisi change à chaque tour.
     */
    public static Stream<Niveau> sequence(List<ConceptRequis> concepts, PlacementStrategy strategie) {
        PriorityQueue<Avancement> file = new PriorityQueue<>(Comparator.comparing(Avancement::courant,
                strategie.priorite().thenComparing(EGALITES)));
        concepts.stream().filter(concept -> concept.nr() > 0).map(concept -> new Avancement(concept, concept))
                .forEach(file::add);
        return Stream.iterate(monterLePrioritaire(file), Objects::nonNull, precedent -> monterLePrioritaire(file));
    }

    /** Monte d'un niveau le concept prioritaire, et le remet dans la file s'il lui en reste ; null si tout est monté. */
    private static Niveau monterLePrioritaire(PriorityQueue<Avancement> file) {
        Avancement choisi = file.poll();
        if (choisi == null) {
            return null;
        }
        ConceptRequis suivant = choisi.courant().unNiveauPlusHaut();
        if (suivant.nr() > 0) {
            file.add(new Avancement(choisi.saisi(), suivant));
        }
        return new Niveau(choisi.saisi(), suivant.actuel());
    }

    /** Préfixe strict : on s'arrête au premier niveau qui ferait dépasser {@code d}, et on oublie tout ce qui suit. */
    public static List<Niveau> prefixe(Stream<Niveau> niveaux, long d) {
        // Java 21 n'a pas d'opération de cumul sur les flux (Gatherers.scan arrive en Java 24) : cumul local
        long[] cumul = { 0 };
        return niveaux.takeWhile(niveau -> (cumul[0] += niveau.cout()) <= d).toList();
    }

    /**
     * Remplit les jours dans l'ordre : chaque niveau est placé d'un seul tenant, quitte à continuer le lendemain.
     * Les niveaux doivent tenir dans le total des disponibilités, ce que garantit {@link #prefixe}.
     */
    public static List<Bloc> placement(List<Niveau> niveaux, List<Integer> disponibilites) {
        verifierDisponibilites(disponibilites);
        return placer(niveaux, disponibilites);
    }

    private static List<Bloc> placer(List<Niveau> niveaux, List<Integer> disponibilites) {
        List<Bloc> blocs = new ArrayList<>();
        int jour = 0;
        int libreCeJour = disponibilites.isEmpty() ? 0 : disponibilites.get(0);
        for (Niveau niveau : niveaux) {
            int aPlacer = niveau.cout();
            while (aPlacer > 0) {
                while (libreCeJour == 0) {
                    jour++;
                    if (jour >= disponibilites.size()) {
                        throw new IllegalArgumentException("Les niveaux dépassent le temps disponible");
                    }
                    libreCeJour = disponibilites.get(jour);
                }
                int duree = Math.min(aPlacer, libreCeJour);
                blocs.add(new Bloc(jour, niveau, duree));
                aPlacer -= duree;
                libreCeJour -= duree;
            }
        }
        return blocs;
    }

    // une boucle plutôt qu'un flux : findFirst() refuse les éléments null, qui sont justement à signaler
    private static void verifierDisponibilites(List<Integer> disponibilites) {
        for (Integer dj : disponibilites) {
            if (dj == null || dj < 0 || dj > DISPONIBILITE_MAX_PAR_JOUR) {
                throw new DonneeInvalideException("Chaque disponibilité doit être entre 0 et "
                        + DISPONIBILITE_MAX_PAR_JOUR + " minutes : " + dj);
            }
        }
    }

    /** Un concept tel qu'il a été saisi, et tel qu'il sera une fois montés les niveaux déjà choisis. */
    private record Avancement(ConceptRequis saisi, ConceptRequis courant) {
    }
}
