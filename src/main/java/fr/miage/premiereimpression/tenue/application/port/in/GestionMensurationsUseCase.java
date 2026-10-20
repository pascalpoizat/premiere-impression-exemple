package fr.miage.premiereimpression.tenue.application.port.in;

import java.util.Map;

import fr.miage.premiereimpression.tenue.domain.model.Mensurations;
import fr.miage.premiereimpression.tenue.domain.model.Mesure;
import fr.miage.premiereimpression.tenue.domain.model.ProfilId;

/** Port d'entrée : mensurations d'un profil (création ou remplacement). */
public interface GestionMensurationsUseCase {

    Mensurations enregistrer(ProfilId profil, Map<Mesure, Double> valeurs);

    /** @throws fr.miage.premiereimpression.tenue.domain.exception.MensurationsIntrouvablesException si le profil ne les a pas renseignées */
    Mensurations trouver(ProfilId profil);
}
