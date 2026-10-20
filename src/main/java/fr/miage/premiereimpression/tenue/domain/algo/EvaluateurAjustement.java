package fr.miage.premiereimpression.tenue.domain.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.miage.premiereimpression.tenue.domain.model.Ajustement;
import fr.miage.premiereimpression.tenue.domain.model.Critere;
import fr.miage.premiereimpression.tenue.domain.model.DetailMesure;
import fr.miage.premiereimpression.tenue.domain.model.Mensurations;
import fr.miage.premiereimpression.tenue.domain.model.Verdict;
import fr.miage.premiereimpression.tenue.domain.model.Vetement;

/**
 * Évalue l'ajustement d'un vêtement sur une personne, critère par critère.
 * <p>
 * Écart asymétrique, avec e = vêtement − corps, a = aisance idéale, s = tolérance serrée, l = échelle d'ampleur :
 * <ul>
 * <li>e &lt; a − s : trop petit, le vêtement est éliminé ;</li>
 * <li>a − s ≤ e &lt; a : un peu juste, pénalité ((a − e) / s)² dans [0, 1] ;</li>
 * <li>e ≥ a : ample, pénalité ((e − a) / l)², sans borne.</li>
 * </ul>
 * Score global : 1 / (1 + moyenne pondérée des pénalités), dans ]0, 1].
 */
public final class EvaluateurAjustement {

    /** Tolérance d'arrondi pour reconnaître un écart exactement idéal. */
    private static final double EPSILON = 1e-9;

    /** @return l'ajustement, ou vide si le vêtement est trop petit pour au moins un critère */
    public Optional<Ajustement> evaluer(Mensurations mensurations, Vetement vetement) {
        List<DetailMesure> details = new ArrayList<>();
        double sommePenalitesPonderees = 0;
        double sommePoids = 0;
        for (Critere critere : vetement.type().criteres()) {
            Optional<DetailMesure> detail = evaluerCritere(critere, mensurations.valeur(critere.mesure()),
                    vetement.mesure(critere.mesure()));
            if (detail.isEmpty()) {
                return Optional.empty();
            }
            details.add(detail.get());
            sommePenalitesPonderees += critere.poids() * detail.get().penalite();
            sommePoids += critere.poids();
        }
        double score = 1 / (1 + sommePenalitesPonderees / sommePoids);
        return Optional.of(new Ajustement(vetement, score, details));
    }

    static Optional<DetailMesure> evaluerCritere(Critere critere, double corps, double vetement) {
        double ecart = vetement - corps;
        double aisance = critere.aisanceIdeale();
        if (ecart < aisance - critere.toleranceSerree() - EPSILON) {
            return Optional.empty();
        }
        Verdict verdict;
        double penalite;
        if (Math.abs(ecart - aisance) <= EPSILON) {
            verdict = Verdict.IDEAL;
            penalite = 0;
        } else if (ecart < aisance) {
            verdict = Verdict.JUSTE;
            penalite = carre((aisance - ecart) / critere.toleranceSerree());
        } else {
            verdict = Verdict.AMPLE;
            penalite = carre((ecart - aisance) / critere.echelleAmpleur());
        }
        return Optional.of(new DetailMesure(critere.mesure(), corps, vetement, ecart, verdict, penalite));
    }

    private static double carre(double x) {
        return x * x;
    }
}
