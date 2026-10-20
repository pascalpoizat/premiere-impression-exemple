package fr.miage.premiereimpression.tenue.domain;

import java.util.EnumMap;
import java.util.Map;

import fr.miage.premiereimpression.tenue.domain.model.Critere;
import fr.miage.premiereimpression.tenue.domain.model.Mensurations;
import fr.miage.premiereimpression.tenue.domain.model.Mesure;
import fr.miage.premiereimpression.tenue.domain.model.ProfilId;
import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;
import fr.miage.premiereimpression.tenue.domain.model.Vetement;
import fr.miage.premiereimpression.tenue.domain.model.VetementId;

/** Fabrique de données de test lisibles. */
public final class DonneesTenue {

    public static final ProfilId MOI = new ProfilId(1);
    public static final ProfilId PRETEUR = new ProfilId(2);

    private DonneesTenue() {
    }

    public static Mensurations corps(ProfilId profil, double poitrine, double taille, double hanches, double jambe,
            double carrure) {
        return new Mensurations(profil, Map.of(Mesure.TOUR_POITRINE, poitrine, Mesure.TOUR_TAILLE, taille,
                Mesure.TOUR_HANCHES, hanches, Mesure.LONGUEUR_JAMBE, jambe, Mesure.CARRURE, carrure));
    }

    /** Mes mensurations de référence. */
    public static Mensurations moi() {
        return corps(MOI, 96, 82, 98, 80, 44);
    }

    /** Vêtement dont chaque mesure vaut corps + aisance idéale + décalage. */
    public static Vetement vetement(long id, ProfilId proprietaire, TypeVetement type, Mensurations corps,
            double decalage) {
        Map<Mesure, Double> mesures = new EnumMap<>(Mesure.class);
        for (Critere critere : type.criteres()) {
            mesures.put(critere.mesure(), corps.valeur(critere.mesure()) + critere.aisanceIdeale() + decalage);
        }
        return new Vetement(new VetementId(id), proprietaire, type, type + " " + id, null, mesures);
    }
}
