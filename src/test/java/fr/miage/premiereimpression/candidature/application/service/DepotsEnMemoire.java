package fr.miage.premiereimpression.candidature.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.miage.premiereimpression.candidature.application.port.out.CandidatureRepository;
import fr.miage.premiereimpression.candidature.application.port.out.ProfilRepository;
import fr.miage.premiereimpression.candidature.domain.model.Candidature;
import fr.miage.premiereimpression.candidature.domain.model.CandidatureId;
import fr.miage.premiereimpression.candidature.domain.model.NouveauProfil;
import fr.miage.premiereimpression.candidature.domain.model.NouvelleCandidature;
import fr.miage.premiereimpression.candidature.domain.model.Profil;
import fr.miage.premiereimpression.candidature.domain.model.ProfilId;

/**
 * Faux adaptateurs en mémoire : les ports de sortie permettent de tester
 * les services sans base de données ni Spring.
 */
final class DepotsEnMemoire {

    private DepotsEnMemoire() {
    }

    static final class Profils implements ProfilRepository {

        private final List<Profil> profils = new ArrayList<>();

        @Override
        public Profil ajouter(NouveauProfil nouveau) {
            Profil profil = new Profil(new ProfilId(profils.size() + 1L), nouveau.pseudo(), nouveau.competences());
            profils.add(profil);
            return profil;
        }

        @Override
        public Optional<Profil> trouver(ProfilId id) {
            return profils.stream().filter(profil -> profil.id().equals(id)).findFirst();
        }

        @Override
        public boolean existePseudo(String pseudo) {
            return profils.stream().anyMatch(profil -> profil.pseudo().equals(pseudo));
        }

        @Override
        public List<Profil> tous() {
            return List.copyOf(profils);
        }
    }

    static final class Candidatures implements CandidatureRepository {

        private final List<Candidature> candidatures = new ArrayList<>();

        @Override
        public Candidature ajouter(NouvelleCandidature nouvelle) {
            Candidature candidature = new Candidature(new CandidatureId(candidatures.size() + 1L), nouvelle.profil(),
                    nouvelle.entreprise(), nouvelle.poste(), nouvelle.texteAnnonce(), nouvelle.dateHeureEntretien(),
                    nouvelle.adresseEntretien());
            candidatures.add(candidature);
            return candidature;
        }

        @Override
        public Optional<Candidature> trouver(CandidatureId id) {
            return candidatures.stream().filter(candidature -> candidature.id().equals(id)).findFirst();
        }

        @Override
        public List<Candidature> tous() {
            return List.copyOf(candidatures);
        }

        @Override
        public List<Candidature> deProfil(ProfilId profil) {
            return candidatures.stream().filter(candidature -> candidature.profil().equals(profil)).toList();
        }

        @Override
        public List<String> textesDesAnnonces() {
            return candidatures.stream().map(Candidature::texteAnnonce).toList();
        }
    }
}
