package tech.chillo.avis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.chillo.avis.entity.Avis;

@Repository
public interface AvisRepository extends JpaRepository<Avis, Integer> {
}
