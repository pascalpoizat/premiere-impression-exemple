package fr.miage.premiereimpression.candidature.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface ProfilJpaRepository extends JpaRepository<ProfilJpaEntity, Long> {

    boolean existsByPseudo(String pseudo);
}
