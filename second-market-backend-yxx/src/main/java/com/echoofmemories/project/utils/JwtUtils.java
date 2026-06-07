package com.echoofmemories.project.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * JWT工具类
 * 
 * @author Echo of Memories
 */
@Slf4j
public class JwtUtils {

    private static final String SECRET = resolveSecret();
    private static final Long EXPIRE_TIME = 7 * 24 * 3600 * 1000L; // 7天 (7 days)
    
    private static String resolveSecret() {
        String fromEnv = System.getenv("JWT_SECRET");
        if (fromEnv == null || fromEnv.isBlank()) {
            log.warn("JWT_SECRET 环境变量未设置！请在生产环境中配置强密钥");
            // 生成临时随机密钥，防止使用硬编码密钥
            return generateTemporarySecret();
        }
        String secret = fromEnv.trim();
        if (secret.length() < 32) {
            log.warn("JWT_SECRET 长度不足32位，建议使用至少32位的随机字符串");
        }
        return secret;
    }
    
    private static String generateTemporarySecret() {
        // 仅用于开发环境，生产环境必须配置环境变量
        try {
            return java.util.Base64.getEncoder().encodeToString(
                java.security.SecureRandom.getInstanceStrong().generateSeed(32)
            );
        } catch (Exception e) {
            log.error("生成临时密钥失败", e);
            // 最后的安全网
            return "dev-mode-temp-key-" + System.currentTimeMillis();
        }
    }

    /**
     * 生成token
     */
    public static String generateToken(String userId, String username) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + EXPIRE_TIME);

        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withIssuedAt(now)
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC256(SECRET));
    }

    /**
     * 创建token（只需要用户ID）
     */
    public static String createToken(String userId) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + EXPIRE_TIME);

        return JWT.create()
                .withClaim("userId", userId)
                .withIssuedAt(now)
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC256(SECRET));
    }

    /**
     * 验证token
     */
    public static boolean verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.error("JWT验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从token中获取用户ID
     */
    public static String getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userId").asString();
        } catch (JWTDecodeException e) {
            log.error("JWT解码失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从token中获取用户名
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            log.error("JWT解码失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取token的过期时间
     */
    public static Date getExpireDate(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getExpiresAt();
        } catch (JWTDecodeException e) {
            log.error("JWT解码失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 判断token是否过期
     */
    public static boolean isExpired(String token) {
        Date expireDate = getExpireDate(token);
        return expireDate != null && expireDate.before(new Date());
    }

}
