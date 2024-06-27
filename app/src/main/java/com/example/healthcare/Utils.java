package com.example.healthcare;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Utils {
    public static String encodeUsername(String username) {
        return Base64.getEncoder().encodeToString(username.getBytes(StandardCharsets.UTF_8));
    }
}
