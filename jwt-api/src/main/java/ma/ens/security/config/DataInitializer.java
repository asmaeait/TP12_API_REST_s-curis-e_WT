package ma.ens.security.config;

import ma.ens.security.entities.Role;
import ma.ens.security.entities.User;
import ma.ens.security.repositories.RoleRepository;
import ma.ens.security.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;

/**
 * Insere des donnees par defaut au premier demarrage
 * Roles et utilisateurs de test sont crees automatiquement
 */
@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final PasswordEncoder passwordEncoder;

    public DataInitializer(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner chargerDonneesInitiales(
            RoleRepository roleRepository,
            UserRepository userRepository) {

        return args -> {

            // Ne pas reinitialiser si des donnees existent deja
            if (roleRepository.count() > 0) {
                logger.info("Donnees deja presentes, initialisation ignoree");
                return;
            }

            // Creation des roles
            Role roleAdmin = roleRepository.save(new Role("ROLE_ADMIN"));
            Role roleUser = roleRepository.save(new Role("ROLE_USER"));
            logger.info("Roles initialises : ROLE_ADMIN, ROLE_USER");

            // Creation du compte administrateur
            User admin = new User();
            admin.setUsername("admin_sys");
            admin.setPassword(passwordEncoder.encode("Admin@2024"));
            admin.setActive(true);
            admin.setRoles(List.of(roleAdmin, roleUser));

            // Creation du compte utilisateur standard
            User etudiant = new User();
            etudiant.setUsername("etudiant");
            etudiant.setPassword(passwordEncoder.encode("Etudiant@123"));
            etudiant.setActive(true);
            etudiant.setRoles(List.of(roleUser));

            userRepository.saveAll(List.of(admin, etudiant));

            logger.info("Comptes crees :");
            logger.info("  admin_sys / Admin@2024 (ADMIN + USER)");
            logger.info("  etudiant / Etudiant@123 (USER)");
        };
    }
}