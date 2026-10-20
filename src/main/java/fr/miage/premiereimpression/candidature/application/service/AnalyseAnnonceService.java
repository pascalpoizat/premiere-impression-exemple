package fr.miage.premiereimpression.candidature.application.service;

import fr.miage.premiereimpression.candidature.application.port.in.AnalyseAnnonceUseCase;
import fr.miage.premiereimpression.candidature.application.port.out.CandidatureRepository;
import fr.miage.premiereimpression.candidature.application.port.out.ProfilRepository;
import fr.miage.premiereimpression.candidature.domain.algo.AnalyseurAnnonce;
import fr.miage.premiereimpression.candidature.domain.exception.CandidatureIntrouvableException;
import fr.miage.premiereimpression.candidature.domain.exception.ProfilIntrouvableException;
import fr.miage.premiereimpression.candidature.domain.model.AnalyseAnnonce;
import fr.miage.premiereimpression.candidature.domain.model.Candidature;
import fr.miage.premiereimpression.candidature.domain.model.CandidatureId;
import fr.miage.premiereimpression.candidature.domain.model.Profil;

public class AnalyseAnnonceService implements AnalyseAnnonceUseCase {

    private final CandidatureRepository candidatures;
    private final ProfilRepository profils;
    private final AnalyseurAnnonce analyseur = new AnalyseurAnnonce();

    public AnalyseAnnonceService(CandidatureRepository candidatures, ProfilRepository profils) {
        this.candidatures = candidatures;
        this.profils = profils;
    }

    @Override
    public AnalyseAnnonce analyser(CandidatureId id, int k) {
        Candidature candidature = candidatures.trouver(id).orElseThrow(() -> new CandidatureIntrouvableException(id));
        Profil profil = profils.trouver(candidature.profil())
                .orElseThrow(() -> new ProfilIntrouvableException(candidature.profil()));
        // v0.1.0 : corpus rechargé à chaque analyse (voir limites dans doc/features)
        return analyseur.analyser(candidature.texteAnnonce(), profil.competences(),
                candidatures.textesDesAnnonces(), k);
    }
}
