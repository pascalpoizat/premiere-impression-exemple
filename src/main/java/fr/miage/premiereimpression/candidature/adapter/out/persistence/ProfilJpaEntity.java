package fr.miage.premiereimpression.candidature.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Représentation en base d'un profil. Ne sort jamais de l'adaptateur. */
@Entity
@Table(name = "profil")
class ProfilJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String pseudo;

    @Column(nullable = false, length = 5000)
    private String competences;

    protected ProfilJpaEntity() {
        // requis par JPA
    }

    ProfilJpaEntity(String pseudo, String competences) {
        this.pseudo = pseudo;
        this.competences = competences;
    }

    Long getId() {
        return id;
    }

    String getPseudo() {
        return pseudo;
    }

    String getCompetences() {
        return competences;
    }
}
