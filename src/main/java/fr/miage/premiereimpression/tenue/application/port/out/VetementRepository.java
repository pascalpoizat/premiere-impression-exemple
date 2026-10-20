package fr.miage.premiereimpression.tenue.application.port.out;

import java.util.List;

import fr.miage.premiereimpression.tenue.domain.model.NouveauVetement;
import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;
import fr.miage.premiereimpression.tenue.domain.model.Vetement;

/** Port de sortie : persistance des vêtements. */
public interface VetementRepository {

    Vetement ajouter(NouveauVetement vetement);

    List<Vetement> tous();

    List<Vetement> deType(TypeVetement type);
}
