package ma.ens.security.repositories;

import ma.ens.security.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository pour acceder aux roles en base de donnees
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Recherche un role par son nom exact
    Role findByName(String name);

    // Verifie si un role existe deja
    boolean existsByName(String name);
}