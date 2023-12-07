package tech.chillo.avis.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tech.chillo.avis.entity.Utilisateur;
import tech.chillo.avis.entity.Validation;
import tech.chillo.avis.repository.ValidationRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ValidationService {
    private final ValidationRepository validationRepository;
    private final NotificationService notificationService;

    public void enregistrer(Utilisateur utilisateur){
        Validation validation = new Validation();
        validation.setUtilisateur(utilisateur);
        Instant creation = Instant.now();
        validation.setCreation(creation);
        validation.setExpiration(creation.plus(10, ChronoUnit.MINUTES));

        Random random = new Random();
        String code = String.format("%06d", random.nextInt(999999));
        validation.setCode(code);

        validationRepository.save(validation);
        //notificationService.envoyer(validation);
    }

    public Validation lireEnFonctionDuCode(String code) throws Exception {
        return validationRepository.findByCode(code).orElseThrow(() -> new Exception("Code not found"));
    }

    //@Scheduled(cron = "0/30 * * * * *") // chaque 30 sec
    public void nettoyer(){
        log.info("Suppression des codes expirés à {} ", Instant.now());
        validationRepository.deleteAllByExpirationBefore(Instant.now());
    }
}
