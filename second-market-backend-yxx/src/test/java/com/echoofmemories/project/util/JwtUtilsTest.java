package com.echoofmemories.project.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 工具类测试
 *
 * @author Echo of Memories
 */
public class JwtUtilsTest {

    @Test
    public void testGenerateToken() {
        String userId = "10001";
        String username = "testuser";
        String token = JwtUtils.generateToken(userId, username);
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    public void testGetUserIdFromToken() {
        String userId = "12345";
        String username = "johndoe";
        String token = JwtUtils.generateToken(userId, username);
        
        String extractedUserId = JwtUtils.getUserId(token);
        assertEquals(userId, extractedUserId);
    }

    @Test
    public void testGetUsernameFromToken() {
        String userId = "67890";
        String username = "janedoe";
        String token = JwtUtils.generateToken(userId, username);
        
        String extractedUsername = JwtUtils.getUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    public void testValidateToken() {
        String userId = "55555";
        String username = "testuser";
        String token = JwtUtils.generateToken(userId, username);
        
        boolean isValid = JwtUtils.validateToken(token);
        assertTrue(isValid);
    }

    @Test
    public void testInvalidToken() {
        String invalidToken = "invalid-token-string";
        
        boolean isValid = JwtUtils.validateToken(invalidToken);
        assertFalse(isValid);
        
        String userId = JwtUtils.getUserId(invalidToken);
        assertNull(userId);
        
        String username = JwtUtils.getUsername(invalidToken);
        assertNull(username);
    }

    @Test
    public void testNullToken() {
        assertFalse(JwtUtils.validateToken(null));
        assertNull(JwtUtils.getUserId(null));
        assertNull(JwtUtils.getUsername(null));
    }

    @Test
    public void testEmptyToken() {
        assertFalse(JwtUtils.validateToken(""));
        assertNull(JwtUtils.getUserId(""));
        assertNull(JwtUtils.getUsername(""));
    }
}

