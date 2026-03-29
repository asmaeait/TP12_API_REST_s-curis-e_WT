package ma.ens.security.repositories;

import ma.ens.security.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository pour acceder aux utilisateurs en base de donnees
 * Herite de JpaRepository qui fournit les operations CRUD de base
 */
public interface UserRepository extends JpaRepository<User, Long> {

    // Utilisee par Spring Security pour charger l'utilisateur au login
    Optional<User> findByUsername(String username);

    // Verifie si un nom d'utilisateur est deja pris
    boolean existsByUsername(String username);
}