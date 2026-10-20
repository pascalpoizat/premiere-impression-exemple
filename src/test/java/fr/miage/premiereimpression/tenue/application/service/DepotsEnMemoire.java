package fr.miage.premiereimpression.tenue.application.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import fr.miage.premiereimpression.tenue.application.port.out.MensurationsRepository;
import fr.miage.premiereimpression.tenue.application.port.out.ProfilsConnus;
import fr.miage.premiereimpression.tenue.application.port.out.VetementRepository;
import fr.miage.premiereimpression.tenue.domain.model.Mensurations;
import fr.miage.premiereimpression.tenue.domain.model.NouveauVetement;
import fr.miage.premiereimpression.tenue.domain.model.ProfilId;
import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;
import fr.miage.premiereimpression.tenue.domain.model.Vetement;
import fr.miage.premiereimpression.tenue.domain.model.VetementId;

/** Faux adaptateurs en mémoire : tests des services sans base de données ni Spring. */
final class DepotsEnMemoire {

    private DepotsEnMemoire() {
    }

    static final class Profils implements ProfilsConnus {

        private final Set<ProfilId> connus = new HashSet<>();

        Profils avec(ProfilId... profils) {
            connus.addAll(List.of(profils));
            return this;
        }

        @Override
        public boolean existe(ProfilId profil) {
            return connus.contains(profil);
        }
    }

    static final class MensurationsEnMemoire implements MensurationsRepository {

        private final Map<ProfilId, Mensurations> parProfil = new HashMap<>();

        @Override
        public Mensurations enregistrer(Mensurations mensurations) {
            parProfil.put(mensurations.profil(), mensurations);
            return mensurations;
        }

        @Override
        public Optional<Mensurations> trouver(ProfilId profil) {
            return Optional.ofNullable(parProfil.get(profil));
        }
    }

    static final class Vetements implements VetementRepository {

        private final List<Vetement> vetements = new ArrayList<>();

        @Override
        public Vetement ajouter(NouveauVetement nouveau) {
            Vetement vetement = new Vetement(new VetementId(vetements.size() + 1L), nouveau.proprietaire(),
                    nouveau.type(), nouveau.nom(), nouveau.description(), nouveau.mesures());
            vetements.add(vetement);
            return vetement;
        }

        @Override
        public List<Vetement> tous() {
            return List.copyOf(vetements);
        }

        @Override
        public List<Vetement> deType(TypeVetement type) {
            return vetements.stream().filter(vetement -> vetement.type() == type).toList();
        }
    }
}
