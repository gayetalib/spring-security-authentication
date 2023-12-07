package tech.chillo.avis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tech.chillo.avis.entity.Avis;
import tech.chillo.avis.entity.Utilisateur;
import tech.chillo.avis.repository.AvisRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvisService {
    private final AvisRepository avisRepository;

    public void createAvis(Avis avis){
        Utilisateur userConnected = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        avis.setUtilisateur(userConnected);
        avisRepository.save(avis);
    }

    public List<Avis> listeAvis() {
         return avisRepository.findAll();
    }
}
