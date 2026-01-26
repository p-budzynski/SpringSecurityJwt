package pl.kurs.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {
    private static final String SECRET = "D0nL4JKs9MxZp9DBt2HT/j54Ul+j9kZ3icVTMeLEe+w=";

    public String generateToken(String login) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, login);
    }

    private String createToken(Map<String, Object> claims, String login) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(login)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*30))
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignatureKey() {
        byte[] key = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(key);
    }

    public boolean validateToken(String token, UserDetails user) {
        String tokenUserName = extractUserName(token);
        return tokenUserName.equals(user.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUserName(String token) {
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
