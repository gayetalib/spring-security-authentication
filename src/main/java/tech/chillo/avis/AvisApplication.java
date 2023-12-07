package tech.chillo.avis;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import tech.chillo.avis.entity.TypeDeRole;
import tech.chillo.avis.entity.Utilisateur;
import tech.chillo.avis.repository.RoleRepository;
import tech.chillo.avis.repository.UtilisateurRepository;

@EnableScheduling
@SpringBootApplication
@AllArgsConstructor
public class AvisApplication implements CommandLineRunner {
	UtilisateurRepository utilisateurRepository;
	PasswordEncoder passwordEncoder;
	RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(AvisApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Utilisateur admin = Utilisateur.builder()
				.actif(true)
				.name("admin")
				.password(passwordEncoder.encode("admin"))
				.email("admin@pts.sn")
				.role(
					roleRepository.findByLibelle(TypeDeRole.ADMIN)
				)
				.build();
		admin = utilisateurRepository.findByEmail("admin@pts.sn")
				.orElse(admin);
		utilisateurRepository.save(admin);

		Utilisateur manager = Utilisateur.builder()
				.actif(true)
				.name("manager")
				.password(passwordEncoder.encode("manager"))
				.email("manager@pts.sn")
				.role(
						roleRepository.findByLibelle(TypeDeRole.MANAGER)
				)
				.build();
		manager = utilisateurRepository.findByEmail("admin@pts.sn")
				.orElse(manager);
		utilisateurRepository.save(manager);
	}
}
