package tech.chillo.avis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.chillo.avis.entity.Utilisateur;
import tech.chillo.avis.entity.Validation;
import tech.chillo.avis.repository.ValidationRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ValidationService {
    private final ValidationRepository validationRepository;
    private final NotificationService notificationService;

    public void enregistrer(Utilisateur utilisateur){
        Validation validation = new Validation();
        validation.setUtilisateur(utilisateur);
        Instant creation = Instant.now();
        validation.setCreation(creation);
        validation.setExpiration(creation.plus(10, ChronoUnit.MINUTES));
        //validation.setActivation();

        Random random = new Random();
        String code = String.format("%06d", random.nextInt(999999));
        validation.setCode(code);

        validationRepository.save(validation);
        notificationService.envoyer(validation);
    }

    public Validation lireEnFonctionDuCode(String code) throws Exception {
        return validationRepository.findByCode(code).orElseThrow(() -> new Exception("Code not found"));
    }
}
