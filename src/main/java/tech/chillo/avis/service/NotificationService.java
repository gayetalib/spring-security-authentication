package tech.chillo.avis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import tech.chillo.avis.entity.Validation;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender javaMailSender;

    public void envoyer(Validation validation){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("gayetalib90@gmail.com");
        mailMessage.setTo(validation.getUtilisateur().getEmail());
        mailMessage.setSubject("Votre code d'activation");

        String text = String.format("Bonjour %s, <br/> Votre code d'activation est %s; A bientot",
                validation.getUtilisateur().getName(), validation.getCode());
        mailMessage.setText(text);

        javaMailSender.send(mailMessage);
    }
}
