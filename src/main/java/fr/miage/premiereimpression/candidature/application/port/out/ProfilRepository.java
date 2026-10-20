package fr.miage.premiereimpression.candidature.application.port.out;

import java.util.List;
import java.util.Optional;

import fr.miage.premiereimpression.candidature.domain.model.NouveauProfil;
import fr.miage.premiereimpression.candidature.domain.model.Profil;
import fr.miage.premiereimpression.candidature.domain.model.ProfilId;

/** Port de sortie : persistance des profils. */
public interface ProfilRepository {

    Profil ajouter(NouveauProfil profil);

    Optional<Profil> trouver(ProfilId id);

    boolean existePseudo(String pseudo);

    List<Profil> tous();
}
