package fr.miage.premiereimpression.candidature.application.port.in;

import fr.miage.premiereimpression.candidature.domain.model.AnalyseAnnonce;
import fr.miage.premiereimpression.candidature.domain.model.CandidatureId;

/** Port d'entrée : analyse d'une annonce au regard du profil qui a candidaté. */
public interface AnalyseAnnonceUseCase {

    /**
     * @param k nombre de mots-clés retenus
     * @throws fr.miage.premiereimpression.candidature.domain.exception.CandidatureIntrouvableException si la candidature n'existe pas
     * @throws fr.miage.premiereimpression.candidature.domain.exception.DonneeInvalideException          si k est hors bornes
     */
    AnalyseAnnonce analyser(CandidatureId candidature, int k);
}
