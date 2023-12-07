package tech.chillo.avis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.chillo.avis.entity.Utilisateur;
import tech.chillo.avis.service.UtilisateurService;

import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UtilisateurController {
    private final UtilisateurService utilisateurService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping
    public List<Utilisateur> getListUser(){
        return utilisateurService.getListe();
    }
}
