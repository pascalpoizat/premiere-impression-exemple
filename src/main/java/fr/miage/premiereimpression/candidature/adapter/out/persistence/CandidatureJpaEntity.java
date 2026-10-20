package fr.miage.premiereimpression.candidature.adapter.out.persistence;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Représentation en base d'une candidature. Le profil est un simple identifiant :
 * l'intégrité référentielle est assurée par la clé étrangère du schéma.
 */
@Entity
@Table(name = "candidature")
class CandidatureJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profil_id", nullable = false)
    private long profilId;

    @Column(nullable = false, length = 120)
    private String entreprise;

    @Column(nullable = false, length = 120)
    private String poste;

    @Column(name = "texte_annonce", nullable = false, length = 20_000)
    private String texteAnnonce;

    @Column(name = "date_heure_entretien")
    private LocalDateTime dateHeureEntretien;

    @Column(name = "adresse_entretien")
    private String adresseEntretien;

    protected CandidatureJpaEntity() {
        // requis par JPA
    }

    CandidatureJpaEntity(long profilId, String entreprise, String poste, String texteAnnonce,
            LocalDateTime dateHeureEntretien, String adresseEntretien) {
        this.profilId = profilId;
        this.entreprise = entreprise;
        this.poste = poste;
        this.texteAnnonce = texteAnnonce;
        this.dateHeureEntretien = dateHeureEntretien;
        this.adresseEntretien = adresseEntretien;
    }

    Long getId() {
        return id;
    }

    long getProfilId() {
        return profilId;
    }

    String getEntreprise() {
        return entreprise;
    }

    String getPoste() {
        return poste;
    }

    String getTexteAnnonce() {
        return texteAnnonce;
    }

    LocalDateTime getDateHeureEntretien() {
        return dateHeureEntretien;
    }

    String getAdresseEntretien() {
        return adresseEntretien;
    }
}
