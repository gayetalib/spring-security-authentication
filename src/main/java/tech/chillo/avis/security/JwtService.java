package tech.chillo.avis.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tech.chillo.avis.entity.Jwt;
import tech.chillo.avis.entity.Utilisateur;
import tech.chillo.avis.repository.JwtRepository;
import tech.chillo.avis.service.UtilisateurService;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class JwtService {

    private static final String BEARER = "bearer";
    private static final String ENCRIPTION_KEY = "608f36e92dc66d97d5933f0e6371493cb4fc05b1aa8f8de64014732472303a7c";
    private final UtilisateurService utilisateurService;
    private final JwtRepository jwtRepository;

    public Jwt getTokenByValue(String valuer) {
        return jwtRepository
                .findByValeur(valuer)
                .orElseThrow(()-> new RuntimeException("Token inconnu !"));
    }

    public Map<String, String> generate(String username) {
        Utilisateur utilisateur = this.utilisateurService.loadUserByUsername(username);
        disableTokens(utilisateur);
        Map<String, String> jwtMap = this.generateJwt(utilisateur);

        Jwt jwt = Jwt.builder()
                .valeur(jwtMap.get(BEARER))
                .desactive(false)
                .expired(false)
                .utilisateur(utilisateur)
                .build();
        jwtRepository.save(jwt);
        return jwtMap;
    }

    public String extractUsername(String token) {
        return this.getClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        return this.getClaim(token, Claims::getExpiration);
    }

    private <T> T getClaim(String token, Function<Claims, T> function) {
        Claims claims = getAllClaims(token);
        return function.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Map<String, String> generateJwt(Utilisateur utilisateur) {
        final long currentTime = System.currentTimeMillis();
        final long expirationTime = currentTime + 30 * 60 * 1000; // 30min converted in 30ms

        final Map<String, Object> claims = Map.of(
                "nom", utilisateur.getName(),
                "role", utilisateur.getRole().getLibelle(),
                Claims.EXPIRATION, new Date(expirationTime),
                Claims.SUBJECT, utilisateur.getEmail()
        );

        final String bearer = Jwts.builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(expirationTime))
                .setSubject(utilisateur.getEmail())
                .setClaims(claims)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
        return Map.of(BEARER, bearer);
    }

    private Key getKey() {
        final byte[] decoder = Decoders.BASE64.decode(ENCRIPTION_KEY);
        return Keys.hmacShaKeyFor(decoder);
    }

    public void deconnexion(){
        Utilisateur userConnected = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Jwt jwt = jwtRepository
                .findByUtilisateurEmailAndDesactiveAndExpired(
                        userConnected.getEmail(),
                        false,
                        false
                ).orElseThrow(() -> new RuntimeException("Token invalide"));
        jwt.setExpired(true);
        jwt.setDesactive(true);
        jwtRepository.save(jwt);
    }

    private void disableTokens(Utilisateur user){
        List<Jwt> jwtList = jwtRepository.findAllByUtilisateurEmail(user.getEmail())
                .peek(
                        jwt -> {
                            jwt.setDesactive(true);
                            jwt.setExpired(true);
                        }
                ).collect(Collectors.toList());
        jwtRepository.saveAll(jwtList);
    }

}