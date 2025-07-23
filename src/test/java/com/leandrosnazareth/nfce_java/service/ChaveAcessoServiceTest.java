package com.leandrosnazareth.nfce_java.service;

import com.leandrosnazareth.nfce_java.config.NfceConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChaveAcessoServiceTest {

    @InjectMocks
    private ChaveAcessoService chaveAcessoService;

    @Test
    void deveGerarChaveAcessoNfceCorretamente() {
        // Given
        Integer uf = 35; // São Paulo
        LocalDateTime dataEmissao = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        String cnpj = "12345678000195";
        Integer serie = 1;
        Integer numero = 123;
        Integer tipoEmissao = 1;

        // When
        String chaveAcesso = chaveAcessoService.gerarChaveAcessoNfce(
            uf, dataEmissao, cnpj, serie, numero, tipoEmissao
        );

        // Then
        assertNotNull(chaveAcesso);
        assertEquals(44, chaveAcesso.length());
        assertTrue(chaveAcesso.matches("\\d{44}"));
        
        // Verifica se a chave começa com os dados corretos
        assertTrue(chaveAcesso.startsWith("35")); // UF
        assertTrue(chaveAcesso.startsWith("3524", 2)); // AAMM (2024-01)
        assertTrue(chaveAcesso.contains(cnpj)); // CNPJ
    }

    @Test
    void deveValidarChaveAcessoCorretamente() {
        // Given
        String chaveAcessoValida = "35240112345678000195650010000001231000000010";
        String chaveAcessoInvalida = "35240112345678000195650010000001231000000011";

        // When & Then
        assertTrue(chaveAcessoService.validarChaveAcesso(chaveAcessoValida));
        assertFalse(chaveAcessoService.validarChaveAcesso(chaveAcessoInvalida));
        assertFalse(chaveAcessoService.validarChaveAcesso(null));
        assertFalse(chaveAcessoService.validarChaveAcesso("123"));
    }

    @Test
    void deveFormatarChaveAcessoCorretamente() {
        // Given
        String chaveAcesso = "35240112345678000195650010000001231000000010";

        // When
        String chaveFormatada = chaveAcessoService.formatarChaveAcesso(chaveAcesso);

        // Then
        assertEquals("3524 0112 3456 7800 0195 6500 1000 0001 2310 0000 0010", chaveFormatada);
    }
}
