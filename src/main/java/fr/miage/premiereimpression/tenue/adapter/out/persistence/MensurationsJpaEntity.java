package fr.miage.premiereimpression.tenue.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Représentation en base des mensurations : une ligne par profil, une colonne par mesure. */
@Entity
@Table(name = "mensurations")
class MensurationsJpaEntity {

    @Id
    @Column(name = "profil_id")
    private Long profilId;

    @Column(name = "tour_poitrine", nullable = false)
    private double tourPoitrine;

    @Column(name = "tour_taille", nullable = false)
    private double tourTaille;

    @Column(name = "tour_hanches", nullable = false)
    private double tourHanches;

    @Column(name = "longueur_jambe", nullable = false)
    private double longueurJambe;

    @Column(nullable = false)
    private double carrure;

    protected MensurationsJpaEntity() {
        // requis par JPA
    }

    MensurationsJpaEntity(long profilId, double tourPoitrine, double tourTaille, double tourHanches,
            double longueurJambe, double carrure) {
        this.profilId = profilId;
        this.tourPoitrine = tourPoitrine;
        this.tourTaille = tourTaille;
        this.tourHanches = tourHanches;
        this.longueurJambe = longueurJambe;
        this.carrure = carrure;
    }

    Long getProfilId() {
        return profilId;
    }

    double getTourPoitrine() {
        return tourPoitrine;
    }

    double getTourTaille() {
        return tourTaille;
    }

    double getTourHanches() {
        return tourHanches;
    }

    double getLongueurJambe() {
        return longueurJambe;
    }

    double getCarrure() {
        return carrure;
    }
}
