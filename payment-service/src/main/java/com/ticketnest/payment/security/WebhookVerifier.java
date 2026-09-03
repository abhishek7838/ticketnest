package com.ticketnest.payment.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Component
public class WebhookVerifier {

    // shared secret, known only to us and the gateway (from config, never hardcoded in real life)
    @Value("${payment.webhook.secret}")
    private String secret;

    /**
     * Recompute the signature over the message and compare it, in constant time,
     * to the signature the caller sent. Returns true only if they match.
     */
    public boolean isValid(String message, String providedSignature) {
        String expected = sign(message);
        // constant-time compare avoids leaking info via timing
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                providedSignature.getBytes(StandardCharsets.UTF_8));
    }

    /** HMAC-SHA256 of the message using the shared secret, as a hex string. */
    public String sign(String message) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] raw = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(raw);   // bytes -> hex text
        } catch (Exception e) {
            throw new IllegalStateException("Failed to sign webhook payload", e);
        }
    }
}