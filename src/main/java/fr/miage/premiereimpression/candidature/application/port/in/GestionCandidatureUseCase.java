package fr.miage.premiereimpression.candidature.application.port.in;

import java.util.List;

import fr.miage.premiereimpression.candidature.domain.model.Candidature;
import fr.miage.premiereimpression.candidature.domain.model.CandidatureId;
import fr.miage.premiereimpression.candidature.domain.model.ProfilId;

/** Port d'entrée : candidatures (modèle commun). */
public interface GestionCandidatureUseCase {

    Candidature creerCandidature(CreerCandidatureCommande commande);

    Candidature trouverCandidature(CandidatureId id);

    List<Candidature> listerCandidatures();

    List<Candidature> listerCandidaturesDe(ProfilId profil);
}
