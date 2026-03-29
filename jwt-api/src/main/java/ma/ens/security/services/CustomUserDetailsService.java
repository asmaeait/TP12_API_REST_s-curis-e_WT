package ma.ens.security.services;

import ma.ens.security.entities.User;
import ma.ens.security.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

/**
 * Service qui charge les utilisateurs depuis la base de donnees
 * C'est le pont entre notre base de donnees et Spring Security
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Methode appelee automatiquement par Spring Security lors du login
     * Charge l'utilisateur depuis la base et le convertit en UserDetails
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        logger.info("Tentative de chargement de l'utilisateur : {}", username);

        // Recherche de l'utilisateur dans la base
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Utilisateur introuvable : {}", username);
                    return new UsernameNotFoundException("Compte introuvable : " + username);
                });

        // Conversion des roles en autorites Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isActive(),
                true,
                true,
                true,
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );
    }
}