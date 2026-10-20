package fr.miage.premiereimpression.preparation.domain;

import java.util.List;
import java.util.stream.Collectors;

import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequis;
import fr.miage.premiereimpression.preparation.domain.model.ConceptRequisId;
import fr.miage.premiereimpression.preparation.domain.model.Niveau;

/** Jeux de données des tests de F3, avec les notations de la fiche : le concept {@code Cᵢ} a le rang {@code i}. */
public final class DonneesPreparation {

    public static final CandidatureId CANDIDATURE = new CandidatureId(1);

    private DonneesPreparation() {
    }

    /** {@code Cᵢ : actuel → requis, qtn} */
    public static ConceptRequis c(int i, int actuel, int requis, int qtn) {
        return new ConceptRequis(new ConceptRequisId(i), CANDIDATURE, "C" + i, actuel, requis, qtn, i);
    }

    /** Écrit une séquence de niveaux comme dans la fiche : {@code C2→2, C2→3, …} */
    public static String notation(List<Niveau> niveaux) {
        return niveaux.stream().map(n -> n.concept().nom() + "→" + n.k()).collect(Collectors.joining(", "));
    }
}
