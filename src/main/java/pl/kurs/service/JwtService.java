package pl.kurs.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pl.kurs.entity.Role;
import pl.kurs.entity.User;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtService {
    private static final String SECRET = "D0nL4JKs9MxZp9DBt2HT/j54Ul+j9kZ3icVTMeLEe+w=";

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        Set<String> roles = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
        claims.put("roles", roles);
        return createToken(claims, user.getUsername());
    }

    @SuppressWarnings("unchecked")
    public List<Role> extractRoles(String token) {
        return extractAllClaims(token).get("roles", List.class)
                .stream()
                .map(s -> Role.valueOf((String) s))
                .toList();
    }

    private String createToken(Map<String, Object> claims, String username) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*30))
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignatureKey() {
        byte[] key = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(key);
    }

    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsTFunction) {
        Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
