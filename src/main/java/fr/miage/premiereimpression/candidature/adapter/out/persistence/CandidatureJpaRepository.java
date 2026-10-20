package fr.miage.premiereimpression.candidature.adapter.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface CandidatureJpaRepository extends JpaRepository<CandidatureJpaEntity, Long> {

    List<CandidatureJpaEntity> findByProfilIdOrderById(long profilId);

    /** Ne charge que les textes : le corpus n'a pas besoin du reste des candidatures. */
    @Query("select c.texteAnnonce from CandidatureJpaEntity c")
    List<String> findAllTextesAnnonces();
}
