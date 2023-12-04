package tech.chillo.avis.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.chillo.avis.dto.AuthenticationDTO;
import tech.chillo.avis.entity.Utilisateur;
import tech.chillo.avis.security.JwtService;
import tech.chillo.avis.service.UtilisateurService;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(value = "user", consumes = MediaType.APPLICATION_JSON_VALUE)
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("inscription")
    public void inscription(@RequestBody Utilisateur utilisateur) throws Exception {
        utilisateurService.insciption(utilisateur);
    }

    @PostMapping("activation")
    public void activation(@RequestBody Map<String, String> activation) throws Exception {
        log.info("Activation...!");
        log.info(getClass().getSimpleName() + " Inscription...!");
        utilisateurService.activation(activation);
    }

    @PostMapping("connexion")
    public Map<String, String> connexion(@RequestBody AuthenticationDTO dto) throws Exception {
        log.info("Connexion...!");
        Authentication authentication =  authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );
        if (authentication.isAuthenticated()) return jwtService.generate(dto.username());
        return Collections.emptyMap();
    }

    @PostMapping("deconnexion")
    public Map<String, String> deconnexion() {
        log.info("Déconnexion...!");
        jwtService.deconnexion();
        return Collections.emptyMap();
    }
}
