package fr.miage.premiereimpression.tenue.domain.algo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Sélection des k meilleurs éléments d'une séquence avec un tas borné.
 * <p>
 * Le tas garde au plus k éléments, le moins bon au sommet : chaque nouvel élément y entre,
 * et si le tas déborde on retire son sommet. Complexité O(n log k) en temps et O(k) en mémoire,
 * contre O(n log n) et O(n) pour un tri complet.
 */
public final class MeilleursK {

    private MeilleursK() {
    }

    /**
     * @param ordre ordre « du meilleur au moins bon » ; il doit être total pour que le résultat soit déterministe
     * @return au plus k éléments, du meilleur au moins bon
     */
    public static <T> List<T> parmi(Iterable<T> elements, int k, Comparator<T> ordre) {
        if (k < 1) {
            throw new IllegalArgumentException("k doit être strictement positif : " + k);
        }
        PriorityQueue<T> tas = new PriorityQueue<>(k + 1, ordre.reversed());
        for (T element : elements) {
            tas.add(element);
            if (tas.size() > k) {
                tas.poll();
            }
        }
        List<T> meilleurs = new ArrayList<>(tas);
        meilleurs.sort(ordre);
        return meilleurs;
    }
}
