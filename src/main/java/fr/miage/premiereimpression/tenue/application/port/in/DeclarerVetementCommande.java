package fr.miage.premiereimpression.tenue.application.port.in;

import java.util.Map;

import fr.miage.premiereimpression.tenue.domain.model.Mesure;
import fr.miage.premiereimpression.tenue.domain.model.ProfilId;
import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;

public record DeclarerVetementCommande(ProfilId proprietaire, TypeVetement type, String nom, String description,
        Map<Mesure, Double> mesures) {
}
