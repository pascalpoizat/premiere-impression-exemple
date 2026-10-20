package fr.miage.premiereimpression.preparation.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Représentation en base d'un concept requis (table concept_requis, migration V4). */
@Entity
@Table(name = "concept_requis")
class ConceptRequisJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidature_id", nullable = false)
    private long candidatureId;

    @Column(nullable = false, length = 120)
    private String nom;

    @Column(nullable = false)
    private int actuel;

    @Column(nullable = false)
    private int requis;

    @Column(nullable = false)
    private int qtn;

    @Column(nullable = false)
    private int rang;

    protected ConceptRequisJpaEntity() {
        // requis par JPA
    }

    ConceptRequisJpaEntity(long candidatureId, String nom, int actuel, int requis, int qtn, int rang) {
        this.candidatureId = candidatureId;
        this.nom = nom;
        this.actuel = actuel;
        this.requis = requis;
        this.qtn = qtn;
        this.rang = rang;
    }

    Long getId() {
        return id;
    }

    long getCandidatureId() {
        return candidatureId;
    }

    String getNom() {
        return nom;
    }

    int getActuel() {
        return actuel;
    }

    int getRequis() {
        return requis;
    }

    int getQtn() {
        return qtn;
    }

    int getRang() {
        return rang;
    }
}
