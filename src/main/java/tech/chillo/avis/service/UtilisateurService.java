package tech.chillo.avis.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tech.chillo.avis.entity.TypeDeRole;
import tech.chillo.avis.entity.Utilisateur;
import tech.chillo.avis.entity.Validation;
import tech.chillo.avis.repository.RoleRepository;
import tech.chillo.avis.repository.UtilisateurRepository;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UtilisateurService implements UserDetailsService {
    private UtilisateurRepository utilisateurRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;
    private ValidationService validationService;

    public void insciption(Utilisateur utilisateur) throws Exception {
        String passwordEncoded = passwordEncoder.encode(utilisateur.getPassword());
        utilisateur.setPassword(passwordEncoded);

        Optional<Utilisateur> optionalUtilisateur = utilisateurRepository.findByEmail(utilisateur.getEmail());
        if (optionalUtilisateur.isPresent()) throw new Exception("Votre email est déjà utilisé !");

        utilisateur.setRole(roleRepository.findByLibelle(TypeDeRole.USER));
        Utilisateur user = utilisateurRepository.save(utilisateur);
        validationService.enregistrer(user);
    }

    public void activation(Map<String, String> activation) throws Exception {
        Validation validation = validationService.lireEnFonctionDuCode(activation.get("code"));
        if (Instant.now().isAfter(validation.getExpiration()))
            throw new RuntimeException("Votre code a expiré");

       Utilisateur utilisateur = utilisateurRepository.findById(validation.getUtilisateur().getId()).orElseThrow(()-> new RuntimeException("Utilisateur inconnu !"));
       utilisateur.setActif(true);
       utilisateurRepository.save(utilisateur);
    }

    @Override
    public Utilisateur loadUserByUsername(String username) throws UsernameNotFoundException {
        return utilisateurRepository
                .findByEmail(username)
                .orElseThrow(()-> new UsernameNotFoundException("Auncun utilisateur ne correspond à cet identifiant"));
    }
}