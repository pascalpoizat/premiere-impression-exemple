package fr.miage.premiereimpression.candidature.application.port.out;

import java.util.List;
import java.util.Optional;

import fr.miage.premiereimpression.candidature.domain.model.Candidature;
import fr.miage.premiereimpression.candidature.domain.model.CandidatureId;
import fr.miage.premiereimpression.candidature.domain.model.NouvelleCandidature;
import fr.miage.premiereimpression.candidature.domain.model.ProfilId;

/** Port de sortie : persistance des candidatures. */
public interface CandidatureRepository {

    Candidature ajouter(NouvelleCandidature candidature);

    Optional<Candidature> trouver(CandidatureId id);

    List<Candidature> tous();

    List<Candidature> deProfil(ProfilId profil);

    /** Corpus de l'analyse TF-IDF : le texte de toutes les annonces. */
    List<String> textesDesAnnonces();
}
