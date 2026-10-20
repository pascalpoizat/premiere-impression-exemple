package fr.miage.premiereimpression.preparation.adapter.out.candidature;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Component;

import fr.miage.premiereimpression.candidature.application.port.in.GestionCandidatureUseCase;
import fr.miage.premiereimpression.candidature.domain.exception.CandidatureIntrouvableException;
import fr.miage.premiereimpression.candidature.domain.model.Candidature;
import fr.miage.premiereimpression.preparation.application.port.out.CandidaturesConnues;
import fr.miage.premiereimpression.preparation.domain.model.CandidatureId;

/**
 * Adaptateur entre modules : le module preparation interroge le module candidature
 * par son port d'entrée, sans jamais dépendre de son domaine métier ni de sa base.
 */
@Component
class CandidaturesConnuesAdapter implements CandidaturesConnues {

    private final GestionCandidatureUseCase candidatures;

    CandidaturesConnuesAdapter(GestionCandidatureUseCase candidatures) {
        this.candidatures = candidatures;
    }

    @Override
    public boolean existe(CandidatureId candidature) {
        return trouver(candidature).isPresent();
    }

    @Override
    public Optional<LocalDate> dateEntretien(CandidatureId candidature) {
        return trouver(candidature).map(Candidature::dateHeureEntretien).map(LocalDateTime::toLocalDate);
    }

    private Optional<Candidature> trouver(CandidatureId candidature) {
        try {
            return Optional.of(candidatures.trouverCandidature(
                    new fr.miage.premiereimpression.candidature.domain.model.CandidatureId(candidature.valeur())));
        } catch (CandidatureIntrouvableException e) {
            return Optional.empty();
        }
    }
}
