package fr.miage.premiereimpression.preparation.adapter.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

interface ConceptRequisJpaRepository extends JpaRepository<ConceptRequisJpaEntity, Long> {

    List<ConceptRequisJpaEntity> findByCandidatureIdOrderByRang(long candidatureId);

    boolean existsByCandidatureIdAndNom(long candidatureId, String nom);

    @Query("select coalesce(max(c.rang), 0) from ConceptRequisJpaEntity c where c.candidatureId = :candidatureId")
    int rangMax(long candidatureId);

    @Transactional
    long deleteByIdAndCandidatureId(long id, long candidatureId);
}
