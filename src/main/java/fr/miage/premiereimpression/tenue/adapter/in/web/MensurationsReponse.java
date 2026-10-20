package fr.miage.premiereimpression.tenue.adapter.in.web;

import fr.miage.premiereimpression.tenue.domain.model.Mensurations;
import fr.miage.premiereimpression.tenue.domain.model.Mesure;

record MensurationsReponse(long profilId, double tourPoitrine, double tourTaille, double tourHanches,
        double longueurJambe, double carrure) {

    static MensurationsReponse depuis(Mensurations mensurations) {
        return new MensurationsReponse(mensurations.profil().valeur(), mensurations.valeur(Mesure.TOUR_POITRINE),
                mensurations.valeur(Mesure.TOUR_TAILLE), mensurations.valeur(Mesure.TOUR_HANCHES),
                mensurations.valeur(Mesure.LONGUEUR_JAMBE), mensurations.valeur(Mesure.CARRURE));
    }
}
