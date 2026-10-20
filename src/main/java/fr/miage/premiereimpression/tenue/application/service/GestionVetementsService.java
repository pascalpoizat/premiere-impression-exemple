package fr.miage.premiereimpression.tenue.application.service;

import java.util.List;
import java.util.Optional;

import fr.miage.premiereimpression.tenue.application.port.in.DeclarerVetementCommande;
import fr.miage.premiereimpression.tenue.application.port.in.GestionVetementsUseCase;
import fr.miage.premiereimpression.tenue.application.port.out.ProfilsConnus;
import fr.miage.premiereimpression.tenue.application.port.out.VetementRepository;
import fr.miage.premiereimpression.tenue.domain.model.NouveauVetement;
import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;
import fr.miage.premiereimpression.tenue.domain.model.Vetement;

public class GestionVetementsService implements GestionVetementsUseCase {

    private final VetementRepository vetements;
    private final ProfilsConnus profils;

    public GestionVetementsService(VetementRepository vetements, ProfilsConnus profils) {
        this.vetements = vetements;
        this.profils = profils;
    }

    @Override
    public Vetement declarer(DeclarerVetementCommande commande) {
        NouveauVetement vetement = new NouveauVetement(commande.proprietaire(), commande.type(), commande.nom(),
                commande.description(), commande.mesures());
        VerificationProfil.exiger(profils, vetement.proprietaire());
        return vetements.ajouter(vetement);
    }

    @Override
    public List<Vetement> lister(Optional<TypeVetement> type) {
        return type.map(vetements::deType).orElseGet(vetements::tous);
    }
}
