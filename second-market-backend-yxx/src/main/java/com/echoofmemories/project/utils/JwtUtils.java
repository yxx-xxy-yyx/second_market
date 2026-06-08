package com.echoofmemories.project.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT 工具类
 * <p>
 * 由 Spring 容器管理，密钥通过配置注入而非硬编码。
 * 生产环境必须在 application.yml / 环境变量中设置强密钥。
 * </p>
 *
 * @author Echo of Memories
 */
@Slf4j
@Component
public class JwtUtils {

    /**
     * JWT 签名密钥（从配置中读取，必须至少 32 位）
     */
    @Value("${project.jwt.secret:${JWT_SECRET:second-market-dev-default-secret-key-please-change-me}}")
    private String secret;

    /**
     * Token 有效期（毫秒），默认 7 天
     */
    @Value("${project.jwt.expire-time:604800000}")
    private long expireTime;

    /**
     * 生成 Token（同时写入 userId 与 username）
     */
    public String generateToken(String userId, String username) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireTime);

        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withIssuedAt(now)
                .withExpiresAt(expireDate)
                .sign(getAlgorithm());
    }

    /**
     * 生成 Token（仅写入 userId）
     */
    public String createToken(String userId) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireTime);

        return JWT.create()
                .withClaim("userId", userId)
                .withIssuedAt(now)
                .withExpiresAt(expireDate)
                .sign(getAlgorithm());
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean verify(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            Algorithm algorithm = getAlgorithm();
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.warn("JWT 验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从 Token 解析出 userId
     */
    public String getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userId").asString();
        } catch (JWTDecodeException e) {
            log.error("JWT 解码失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 Token 解析出 username
     */
    public String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            log.error("JWT 解码失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取 Token 过期时间
     */
    public Date getExpireDate(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getExpiresAt();
        } catch (JWTDecodeException e) {
            log.error("JWT 解码失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Token 是否已经过期
     */
    public boolean isExpired(String token) {
        Date expireDate = getExpireDate(token);
        return expireDate != null && expireDate.before(new Date());
    }

    /**
     * 根据密钥生成签名算法（HMAC256）
     */
    private Algorithm getAlgorithm() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT 密钥未配置！请设置 project.jwt.secret 或 JWT_SECRET 环境变量");
        }
        if (secret.length() < 32) {
            log.warn("JWT 密钥长度不足 32 位，建议使用更强的随机字符串");
        }
        return Algorithm.HMAC256(secret);
    }
}
