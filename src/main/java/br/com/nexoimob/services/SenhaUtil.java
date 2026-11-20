package br.com.nexoimob.services;

import java.nio.charset.StandardCharsets;

public final class SenhaUtil {
    private SenhaUtil() {
    }

    public static void validar(String senha) {
        if (senha == null || senha.length() < 10) {
            throw new IllegalArgumentException("Use uma senha com pelo menos 10 caracteres");
        }
        // BCrypt aceita até 72 bytes. Acentos e emojis podem ocupar mais de um byte.
        if (senha.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException(
                    "A senha deve ocupar até 72 bytes. Reduza os caracteres, principalmente acentos e emojis");
        }
    }
}
