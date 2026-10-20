package fr.miage.premiereimpression.tenue.adapter.out.persistence;

import java.util.EnumMap;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.Table;

import fr.miage.premiereimpression.tenue.domain.model.Mesure;
import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;

/** Représentation en base d'un vêtement ; ses mesures sont dans la table mesure_vetement. */
@Entity
@Table(name = "vetement")
class VetementJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proprietaire_id", nullable = false)
    private long proprietaireId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeVetement type;

    @Column(nullable = false, length = 120)
    private String nom;

    @Column(length = 1000)
    private String description;

    // EAGER : l'algorithme a toujours besoin des mesures (v0.1.0 : une requête par vêtement, voir limites)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mesure_vetement", joinColumns = @JoinColumn(name = "vetement_id"))
    @MapKeyColumn(name = "mesure", length = 20)
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "valeur_cm", nullable = false)
    private Map<Mesure, Double> mesures = new EnumMap<>(Mesure.class);

    protected VetementJpaEntity() {
        // requis par JPA
    }

    VetementJpaEntity(long proprietaireId, TypeVetement type, String nom, String description,
            Map<Mesure, Double> mesures) {
        this.proprietaireId = proprietaireId;
        this.type = type;
        this.nom = nom;
        this.description = description;
        this.mesures = new EnumMap<>(mesures);
    }

    Long getId() {
        return id;
    }

    long getProprietaireId() {
        return proprietaireId;
    }

    TypeVetement getType() {
        return type;
    }

    String getNom() {
        return nom;
    }

    String getDescription() {
        return description;
    }

    Map<Mesure, Double> getMesures() {
        return mesures;
    }
}
