package fr.miage.premiereimpression.tenue.application.port.in;

import java.util.List;
import java.util.Optional;

import fr.miage.premiereimpression.tenue.domain.model.Ajustement;
import fr.miage.premiereimpression.tenue.domain.model.ProfilId;
import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;

/** Port d'entrée : vêtements des autres profils qui vont le mieux à un profil. */
public interface RechercheVetementsUseCase {

    /**
     * @throws fr.miage.premiereimpression.tenue.domain.exception.ProfilInconnuException            si le profil n'existe pas
     * @throws fr.miage.premiereimpression.tenue.domain.exception.MensurationsIntrouvablesException si le profil n'a pas de mensurations
     * @throws fr.miage.premiereimpression.tenue.domain.exception.DonneeInvalideException           si k est hors bornes
     */
    List<Ajustement> compatibles(ProfilId profil, int k, Optional<TypeVetement> type);
}
