package main.java.com.jana.utils;

import main.java.com.jana.security.TokenService;

import javax.servlet.http.HttpServletRequest;

public class TokenUtils {
    public static String extrairToken(HttpServletRequest req) {
        String header = req.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }

    public static Integer extrairUserId(HttpServletRequest req) {
        String token = extrairToken(req);
        if (token == null) return null;
        return TokenService.extractUserIdFromToken(token).intValue();
    }
}
