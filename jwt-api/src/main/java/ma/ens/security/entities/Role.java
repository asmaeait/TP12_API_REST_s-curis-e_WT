package ma.ens.security.entities;

import jakarta.persistence.*;

/**
 * Entite representant un role dans le systeme
 * Exemples : ROLE_ADMIN, ROLE_USER
 */
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    // Constructeurs
    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    /**
     * Retourne le nom du role sans le prefixe ROLE_
     * Utile pour l'affichage
     */
    public String getDisplayName() {
        return name != null ? name.replace("ROLE_", "") : "";
    }
}