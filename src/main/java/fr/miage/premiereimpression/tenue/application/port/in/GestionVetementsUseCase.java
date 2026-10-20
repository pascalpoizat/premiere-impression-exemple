package fr.miage.premiereimpression.tenue.application.port.in;

import java.util.List;
import java.util.Optional;

import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;
import fr.miage.premiereimpression.tenue.domain.model.Vetement;

/** Port d'entrée : vêtements proposés au prêt. */
public interface GestionVetementsUseCase {

    Vetement declarer(DeclarerVetementCommande commande);

    List<Vetement> lister(Optional<TypeVetement> type);
}
