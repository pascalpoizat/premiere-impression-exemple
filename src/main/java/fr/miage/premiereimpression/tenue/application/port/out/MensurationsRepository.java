package fr.miage.premiereimpression.tenue.application.port.out;

import java.util.Optional;

import fr.miage.premiereimpression.tenue.domain.model.Mensurations;
import fr.miage.premiereimpression.tenue.domain.model.ProfilId;

/** Port de sortie : persistance des mensurations. */
public interface MensurationsRepository {

    /** Crée ou remplace les mensurations du profil. */
    Mensurations enregistrer(Mensurations mensurations);

    Optional<Mensurations> trouver(ProfilId profil);
}
