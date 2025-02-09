package core.utils;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Утилиты для генерации кодов и проверок в рамках протокола аутентификации OAuth.
 */
public class OauthUtils {
    /**
     * Генерирует случайную строку для использования в аутентификации OAuth.
     *
     * @return строка, представляющая собой случайный код (code verifier)
     */
    @Nonnull
    public static String codeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] codeVerifier = new byte[32];
        secureRandom.nextBytes(codeVerifier);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
    }

    /**
     * Вычисляет код вызова (code challenge) на основе предоставленного кода (code verifier)
     * в соответствии с протоколом аутентификации OAuth.
     *
     * @param codeVerifier строка, представляющая собой код (code verifier)
     * @return строка, представляющая собой код вызова (code challenge)
     */
    @Nonnull
    public static String codeChallenge(String codeVerifier) {
        byte[] bytes;
        MessageDigest messageDigest;
        try {
            bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при вычислении кода вызова.", e);
        }
        messageDigest.update(bytes, 0, bytes.length);
        byte[] digest = messageDigest.digest();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }
}
