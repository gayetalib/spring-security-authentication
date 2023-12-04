package tech.chillo.avis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.chillo.avis.entity.Role;
import tech.chillo.avis.entity.TypeDeRole;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByLibelle(TypeDeRole libelle);
}
