package com.mentoree.config.utils;

import java.util.Map;

public interface JwtUtils {
    String generateToken(Long id, String subject, String authorities);
    Map<String, Object> decode(String token);
    void verify(String token);
}
