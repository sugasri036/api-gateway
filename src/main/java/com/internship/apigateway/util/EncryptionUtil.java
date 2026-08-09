package com.internship.apigateway.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptionUtil {

    public static String encrypt(String text) {
        return Base64.getEncoder()
                .encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    public static String decrypt(String text) {
        return new String(
                Base64.getDecoder().decode(text),
                StandardCharsets.UTF_8
        );
    }
}