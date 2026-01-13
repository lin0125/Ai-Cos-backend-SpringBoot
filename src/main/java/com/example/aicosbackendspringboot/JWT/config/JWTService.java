package com.example.aicosbackendspringboot.JWT.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import com.example.aicosbackendspringboot.JWT.entities.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
    @Value("${jwt.secret-key}")
    private String SECRET_EKY;

    @Value("${jwt.expired.time}")
    private Float EXPIRED_TIME;

    // username = username
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractUserType(String token) {
        Claims claims = extractAllClaims(token);
        Object userTypeObject = claims.get("userType");

        if (userTypeObject == null) {
            return null;
        }

        // 情況 A: 它是 List (例如 ["admin"] 或 [{authority=admin}])
        if (userTypeObject instanceof List) {
            List<?> list = (List<?>) userTypeObject;
            if (!list.isEmpty()) {
                Object firstItem = list.get(0);
                // 如果 List 裡面包的是 Map (例如 Spring Security 常見的 [{authority=admin}])
                if (firstItem instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) firstItem;
                    if (map.containsKey("authority")) {
                        return map.get("authority").toString();
                    }
                }
                // 否則直接轉字串
                return firstItem.toString();
            }
        }
        // 情況 B: 它是 Map (這就是你 Log 顯示的狀況: {authority=admin})
        else if (userTypeObject instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) userTypeObject;
            if (map.containsKey("authority")) {
                return map.get("authority").toString();
            }
        }
        // 情況 C: 它是純字串
        else {
            return userTypeObject.toString();
        }

        return null;
    }

    public String extractUserEmail(String token) {
        final Claims claims = extractAllClaims(token);
        return (String) claims.get("user_email");
    }

    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();

        if (userDetails instanceof UserEntity) {
            claims.put("userType", userDetails.getAuthorities());
            claims.put("user_email", ((UserEntity) userDetails).getUserEmail());
        } else {
            claims.put("userType", "UNKNOWN");
            claims.put("user_email", "null");
        }
        return buildToken(claims, userDetails);
    }

    public String buildToken(
            Map<String,Object> extractClaims,
            UserDetails userDetails )
    {
        return Jwts
                .builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date((long) (System.currentTimeMillis() + EXPIRED_TIME)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_EKY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

