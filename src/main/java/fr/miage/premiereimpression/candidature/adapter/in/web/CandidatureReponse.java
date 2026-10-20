package fr.miage.premiereimpression.candidature.adapter.in.web;

import java.time.LocalDateTime;

import fr.miage.premiereimpression.candidature.domain.model.Candidature;

record CandidatureReponse(long id, long profilId, String entreprise, String poste, String texteAnnonce,
        LocalDateTime dateHeureEntretien, String adresseEntretien) {

    static CandidatureReponse depuis(Candidature candidature) {
        return new CandidatureReponse(candidature.id().valeur(), candidature.profil().valeur(),
                candidature.entreprise(), candidature.poste(), candidature.texteAnnonce(),
                candidature.dateHeureEntretien(), candidature.adresseEntretien());
    }
}
