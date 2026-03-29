package ma.ens.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

/**
 * Utilitaire pour la generation et la validation des tokens JWT
 * Utilise l'algorithme HMAC-SHA256 pour signer les tokens
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    // Cle secrete pour signer les tokens - doit etre longue et complexe
    private final String secretKey = "C3tteC l3SecretJwtKeyPourAuthentification2024!";

    // Duree de validite du token : 1 heure en millisecondes
    private final long tokenDuration = 3600000;

    // Generation de la cle de signature a partir du secret
    private final Key signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());

    /**
     * Genere un token JWT pour un utilisateur authentifie
     * Le token contient le nom d'utilisateur et une date d'expiration
     *
     * @param username le nom de l'utilisateur connecte
     * @return le token JWT signe
     */
    public String generateToken(String username) {
        logger.info("Generation du token JWT pour : {}", username);

        return Jwts.builder()
                .setSubject(username)                                          // Identite de l'utilisateur
                .setIssuedAt(new Date(System.currentTimeMillis()))             // Date de creation
                .setExpiration(new Date(System.currentTimeMillis() + tokenDuration)) // Date d'expiration
                .signWith(signingKey, SignatureAlgorithm.HS256)                // Signature HMAC-SHA256
                .compact();
    }

    /**
     * Extrait le nom d'utilisateur depuis un token JWT
     *
     * @param token le token JWT
     * @return le nom d'utilisateur contenu dans le token
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Verifie si un token JWT est valide et non expire
     *
     * @param token le token a valider
     * @return true si le token est valide, false sinon
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("Token JWT expire : {}", e.getMessage());
        } catch (JwtException e) {
            logger.warn("Token JWT invalide : {}", e.getMessage());
        }
        return false;
    }

    /**
     * Retourne la duree de validite du token en secondes
     */
    public long getTokenDuration() {
        return tokenDuration / 1000;
    }
}