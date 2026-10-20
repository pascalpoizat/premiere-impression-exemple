package fr.miage.premiereimpression.tenue.domain.algo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class MeilleursKTest {

    /** Élément avec des scores souvent égaux : l'identifiant départage (ordre total). */
    private record Note(int id, int score) {
    }

    private static final Comparator<Note> DU_MEILLEUR = Comparator.comparingInt(Note::score).reversed()
            .thenComparingInt(Note::id);

    static LongStream graines() {
        return LongStream.rangeClosed(1, 20);
    }

    @ParameterizedTest(name = "graine {0}")
    @MethodSource("graines")
    void donneLeMemeResultatQuUnTriComplet(long graine) {
        Random hasard = new Random(graine);
        List<Note> notes = new ArrayList<>();
        int n = hasard.nextInt(200);
        for (int i = 0; i < n; i++) {
            notes.add(new Note(i, hasard.nextInt(10)));
        }
        int k = 1 + hasard.nextInt(30);

        List<Note> parTas = MeilleursK.parmi(notes, k, DU_MEILLEUR);
        List<Note> parTri = notes.stream().sorted(DU_MEILLEUR).limit(k).toList();

        assertThat(parTas).isEqualTo(parTri);
    }

    @Test
    void renvoieToutQuandKDepasseLeNombreDElements() {
        List<Note> notes = List.of(new Note(1, 3), new Note(2, 9), new Note(3, 5));

        assertThat(MeilleursK.parmi(notes, 10, DU_MEILLEUR)).extracting(Note::id).containsExactly(2, 3, 1);
    }

    @Test
    void kEgalUnDonneLeMeilleur() {
        List<Note> notes = List.of(new Note(1, 3), new Note(2, 9), new Note(3, 9));

        assertThat(MeilleursK.parmi(notes, 1, DU_MEILLEUR)).containsExactly(new Note(2, 9));
    }

    @Test
    void uneSequenceVideDonneUnResultatVide() {
        assertThat(MeilleursK.parmi(List.<Note>of(), 5, DU_MEILLEUR)).isEmpty();
    }

    @Test
    void refuseUnKNonStrictementPositif() {
        List<Note> notes = List.of();
        assertThatThrownBy(() -> MeilleursK.parmi(notes, 0, DU_MEILLEUR)).isInstanceOf(IllegalArgumentException.class);
    }
}
