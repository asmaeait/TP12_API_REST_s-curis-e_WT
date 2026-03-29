package ma.ens.security.web;

import ma.ens.security.jwt.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * Controleur REST pour l'authentification
 * Expose un endpoint public pour obtenir un token JWT
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService,
                          JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Endpoint de connexion - genere un token JWT si les identifiants sont corrects
     * POST /api/auth/login
     * Body : { "username": "...", "password": "..." }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");

        logger.info("Tentative de connexion pour : {}", username);

        try {
            // Verification des identifiants via Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Chargement des details de l'utilisateur
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Generation du token JWT
            String token = jwtUtil.generateToken(userDetails.getUsername());

            logger.info("Connexion reussie pour : {}", username);

            // Retour du token avec les informations utiles
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "username", username,
                    "type", "Bearer",
                    "expiresIn", jwtUtil.getTokenDuration(),
                    "message", "Authentification reussie"
            ));

        } catch (BadCredentialsException e) {
            logger.warn("Identifiants incorrects pour : {}", username);
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Identifiants incorrects",
                    "message", "Nom d'utilisateur ou mot de passe invalide"
            ));
        }
    }
}