package br.com.nexoimob.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SenhaUtilTest {
    @Test void aceitaSenhaValida() {
        assertDoesNotThrow(() -> SenhaUtil.validar("MinhaSenhaSegura2026!"));
    }
    @Test void rejeitaSenhaCurta() {
        assertThrows(IllegalArgumentException.class, () -> SenhaUtil.validar("123456789"));
    }
    @Test void rejeitaMaisDeSetentaEDoisBytes() {
        assertThrows(IllegalArgumentException.class, () -> SenhaUtil.validar("x".repeat(73)));
    }
}
