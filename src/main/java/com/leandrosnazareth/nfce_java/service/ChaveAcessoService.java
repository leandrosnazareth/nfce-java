package com.leandrosnazareth.nfce_java.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class ChaveAcessoService {

    private static final int[] PESOS = {4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    public String gerarChaveAcesso(Integer uf, LocalDateTime dataEmissao, String cnpj, 
                                  String modelo, Integer serie, Integer numero, 
                                  Integer tipoEmissao, Integer codigoNumerico) {
        
        StringBuilder chave = new StringBuilder();
        
        // UF (2 dígitos)
        chave.append(String.format("%02d", uf));
        
        // Ano e mês (AAMM)
        chave.append(dataEmissao.format(DateTimeFormatter.ofPattern("yyMM")));
        
        // CNPJ (14 dígitos)
        chave.append(cnpj);
        
        // Modelo (2 dígitos)
        chave.append(modelo);
        
        // Série (3 dígitos)
        chave.append(String.format("%03d", serie));
        
        // Número (9 dígitos)
        chave.append(String.format("%09d", numero));
        
        // Tipo de emissão (1 dígito)
        chave.append(tipoEmissao);
        
        // Código numérico (8 dígitos)
        chave.append(String.format("%08d", codigoNumerico));
        
        // Cálculo do dígito verificador
        int digitoVerificador = calcularDigitoVerificador(chave.toString());
        chave.append(digitoVerificador);
        
        return chave.toString();
    }

    public String gerarChaveAcessoNfce(Integer uf, LocalDateTime dataEmissao, String cnpj,
                                      Integer serie, Integer numero, Integer tipoEmissao) {
        
        // Gera um código numérico aleatório de 8 dígitos
        int codigoNumerico = new Random().nextInt(99999999 - 10000000) + 10000000;
        
        return gerarChaveAcesso(uf, dataEmissao, cnpj, "65", serie, numero, tipoEmissao, codigoNumerico);
    }

    private int calcularDigitoVerificador(String chave) {
        int soma = 0;
        
        for (int i = 0; i < chave.length(); i++) {
            int digito = Character.getNumericValue(chave.charAt(i));
            soma += digito * PESOS[i];
        }
        
        int resto = soma % 11;
        
        if (resto < 2) {
            return 0;
        } else {
            return 11 - resto;
        }
    }

    public boolean validarChaveAcesso(String chaveAcesso) {
        if (chaveAcesso == null || chaveAcesso.length() != 44) {
            return false;
        }
        
        try {
            // Verifica se todos os caracteres são dígitos
            Long.parseLong(chaveAcesso);
            
            // Extrai a chave sem o dígito verificador
            String chaveSemDv = chaveAcesso.substring(0, 43);
            int digitoInformado = Character.getNumericValue(chaveAcesso.charAt(43));
            
            // Calcula o dígito verificador
            int digitoCalculado = calcularDigitoVerificador(chaveSemDv);
            
            return digitoInformado == digitoCalculado;
            
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String formatarChaveAcesso(String chaveAcesso) {
        if (chaveAcesso == null || chaveAcesso.length() != 44) {
            return chaveAcesso;
        }
        
        StringBuilder chaveFormatada = new StringBuilder();
        for (int i = 0; i < chaveAcesso.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                chaveFormatada.append(" ");
            }
            chaveFormatada.append(chaveAcesso.charAt(i));
        }
        
        return chaveFormatada.toString();
    }
}
