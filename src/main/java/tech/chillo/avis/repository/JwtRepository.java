package tech.chillo.avis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.chillo.avis.entity.Jwt;

import javax.validation.constraints.Email;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface JwtRepository extends JpaRepository<Jwt, Integer> {
    Optional<Jwt> findByValeur(String valeur);
    Optional<Jwt> findByValeurAndDesactiveAndExpired(String valeur, boolean desactive, boolean expired);
    Optional<Jwt> findByUtilisateurEmailAndDesactiveAndExpired(@Email String utilisateur_email, boolean desactive, boolean expired);

    Stream<Jwt> findAllByUtilisateurEmail(@Email String utilisateur_email);
}
