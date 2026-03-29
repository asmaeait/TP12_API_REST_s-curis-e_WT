package ma.ens.security.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * Controleur de test pour verifier les acces selon les roles
 * Permet de tester le bon fonctionnement du filtre JWT
 */
@RestController
@RequestMapping("/api")
public class TestController {

    /**
     * Endpoint accessible a tous les utilisateurs connectes
     * GET /api/user/profile
     */
    @GetMapping("/user/profile")
    public ResponseEntity<?> userProfile() {

        // Recuperation de l'utilisateur connecte depuis le contexte Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return ResponseEntity.ok(Map.of(
                "message", "Acces autorise - Espace utilisateur",
                "username", auth.getName(),
                "roles", auth.getAuthorities().toString()
        ));
    }

    /**
     * Endpoint accessible uniquement aux administrateurs
     * GET /api/admin/dashboard
     */
    @GetMapping("/admin/dashboard")
    public ResponseEntity<?> adminDashboard() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return ResponseEntity.ok(Map.of(
                "message", "Acces autorise - Espace administrateur",
                "username", auth.getName(),
                "roles", auth.getAuthorities().toString(),
                "info", "Section reservee aux administrateurs"
        ));
    }

    /**
     * Endpoint public pour verifier que l'API fonctionne
     * GET /api/auth/status
     */
    @GetMapping("/auth/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok(Map.of(
                "status", "API operationnelle",
                "message", "Bienvenue sur l'API securisee JWT"
        ));
    }
}