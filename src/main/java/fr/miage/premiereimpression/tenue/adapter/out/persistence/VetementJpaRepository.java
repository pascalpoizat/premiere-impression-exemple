package fr.miage.premiereimpression.tenue.adapter.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.miage.premiereimpression.tenue.domain.model.TypeVetement;

interface VetementJpaRepository extends JpaRepository<VetementJpaEntity, Long> {

    List<VetementJpaEntity> findByTypeOrderById(TypeVetement type);
}
