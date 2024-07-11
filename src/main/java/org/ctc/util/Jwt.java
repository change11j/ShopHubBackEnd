package org.ctc.util;

import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.ctc.dto.GoogleData;
import org.ctc.entity.Users;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

public class Jwt {

    // 生成 JWT
    public static String generateToken(String subject, Integer userId, long ttlMillis, String secretKey) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Date now = new Date();
        Date exp = new Date(now.getTime() + ttlMillis);

        return Jwts.builder()
                .setSubject(subject)
                .claim("userId",userId)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 驗證 JWT
    public static Claims parseToken(String token, String secretKey) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String parseSHA256(String target){
        String shaStr = null ;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(target.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            shaStr=hexString.toString();
        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }
return  shaStr;

    }


    public static GoogleData parseToGooglePayload(String jwt){
        String[] parts =jwt.split("\\.", 0);
                String payload=parts[1];
        byte[] bytes = Base64.getUrlDecoder().decode(payload);
        String decodedString = new String(bytes,StandardCharsets.UTF_8);
        Gson gson=new Gson();
        return gson.fromJson(decodedString, GoogleData.class);
    }

    public static void main(String[] args) {

//       String secretKey = parseSHA256("your-secret-key");
//        System.out.println(secretKey);
        String subject = "user123";
        long ttlMillis = 3600000; // 1 hour
        // 生成 JWT
        String[] parts = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImFkZjVlNzEwZWRmZWJlY2JlZmE5YTYxNDk1NjU0ZDAzYzBiOGVkZjgiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI4NzMwMTc5MDE0NzMtcXRxdDdtbTNlMTlkNWc0a2cxcGZ0NjRicmlpYmxiNTMuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI4NzMwMTc5MDE0NzMtcXRxdDdtbTNlMTlkNWc0a2cxcGZ0NjRicmlpYmxiNTMuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDMxMjcxMzk4Mjg3MjY5OTk5MzciLCJlbWFpbCI6InM4MDUzMnNAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5iZiI6MTcxMTE5NDQzMCwibmFtZSI6Iuael-mWjuW7uiIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS9BQ2c4b2NKMGZkSG13VE5lMEFNSHhGNWFzZU1zV1ZoWl9Bdzd0dXdFQm5YNXFLMGY9czk2LWMiLCJnaXZlbl9uYW1lIjoi6ZaO5bu6IiwiZmFtaWx5X25hbWUiOiLmnpciLCJpYXQiOjE3MTExOTQ3MzAsImV4cCI6MTcxMTE5ODMzMCwianRpIjoiNzg2NWEyNDA2MGEzMThmYTllNDE1ODg1MDdkYTgwMDE2ZjI3ZGMyMyJ9".split("\\.", 0);
         String payload=parts[1];
        byte[] bytes = Base64.getUrlDecoder().decode(payload);
        String decodedString = new String(bytes,StandardCharsets.UTF_8);
        Gson gson=new Gson();
        GoogleData googleData = gson.fromJson(decodedString, GoogleData.class);
        System.out.println(googleData);
//        for (String part : parts) {
//            byte[] bytes = Base64.getUrlDecoder().decode(part);
//            String decodedString = new String(bytes,StandardCharsets.UTF_8);
//
//            System.out.println("Decoded: " + decodedString);
//        }


//        String token = generateToken(subject, ttlMillis, secretKey);
//        System.out.println("Generated JWT: " + token);
//
//        // 驗證 JWT
//        Claims claims = parseToken(token, secretKey);
//        System.out.println("Subject: " + claims.getSubject());
//        System.out.println("Expiration: " + claims.getExpiration());
    }
}