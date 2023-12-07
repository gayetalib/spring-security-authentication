package tech.chillo.avis.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import tech.chillo.avis.entity.Jwt;
import tech.chillo.avis.entity.RefreshToken;
import tech.chillo.avis.entity.Utilisateur;
import tech.chillo.avis.repository.JwtRepository;
import tech.chillo.avis.service.UtilisateurService;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class JwtService {

    private static final String BEARER = "bearer";
    private static final String ENCRIPTION_KEY = "608f36e92dc66d97d5933f0e6371493cb4fc05b1aa8f8de64014732472303a7c";
    public static final String REFRESH = "refresh";
    public static final String TOKEN_INVALIDE = "Token invalide";
    private final UtilisateurService utilisateurService;
    private final JwtRepository jwtRepository;

    public Jwt getTokenByValue(String valuer) {
        return jwtRepository
                .findByValeur(valuer)
                .orElseThrow(()-> new RuntimeException("Token inconnu !"));
    }

    public Map<String, String> generate(String username) {
        Utilisateur utilisateur = this.utilisateurService.loadUserByUsername(username);
        //disableTokens(utilisateur);
        Map<String, String> jwtMap = new java.util.HashMap<>(this.generateJwt(utilisateur));
        RefreshToken refreshToken = RefreshToken.builder()
                .valeur(UUID.randomUUID().toString())
                .expired(false)
                .creation(Instant.now())
                .expiredDate(Instant.now().plusMillis(30 *60 *1000)) //30ms
                .build();

        Jwt jwt = Jwt.builder()
                .valeur(jwtMap.get(BEARER))
                .desactive(false)
                .expired(false)
                .utilisateur(utilisateur)
                .refreshToken(refreshToken)
                .build();
        jwtRepository.save(jwt);
        jwtMap.put(REFRESH, refreshToken.getValeur());
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
        final long expirationTime = currentTime + 5 * 60 * 1000;

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


    //@Scheduled(cron = "0 */1 * * * *") // every minute
    public void removeUseLessJwt(){
        log.info("Suppression des tokens à {} ", Instant.now());
        jwtRepository.deleteAllByExpiredAndDesactive(true, true);
    }

    public Map<String, String> refreshToken(Map<String, String> refreshTokenRequest){
       Jwt jwt = jwtRepository
                .findByRefreshTokenValeur(refreshTokenRequest.get(REFRESH))
                .orElseThrow(() -> new RuntimeException(TOKEN_INVALIDE));
       if (jwt.getRefreshToken().isExpired() || jwt.getRefreshToken().getExpiredDate().isBefore(Instant.now()))
            throw new RuntimeException(TOKEN_INVALIDE);
       disableTokens(jwt.getUtilisateur());
       return generate(jwt.getUtilisateur().getEmail());
    }

}