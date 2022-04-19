package kg.banksystem.deliverybackend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenDecoder {

    @Value("${jwt.token.secret}")
    private String secret;

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public Claims decodeToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public Map<String, String> parseToken(String token) {
        Claims claims = decodeToken(token);
        Map<String, String> tokenDetails = new HashMap<>();
        tokenDetails.put("sub", claims.get("sub").toString());
        tokenDetails.put("user_id", claims.get("user_id").toString());
        tokenDetails.put("user_role", claims.get("user_role").toString());
        tokenDetails.put("iat", claims.get("iat").toString());
        tokenDetails.put("exp", claims.get("exp").toString());
        return tokenDetails;
    }
}