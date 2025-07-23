package com.leandrosnazareth.nfce_java.controller;

import com.leandrosnazareth.nfce_java.model.dto.NfceRequestDto;
import com.leandrosnazareth.nfce_java.model.dto.NfceResponseDto;
import com.leandrosnazareth.nfce_java.service.NfceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/nfce")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "NFC-e", description = "APIs para gerenciamento de NFC-e")
public class NfceController {

    private final NfceService nfceService;

    @PostMapping
    @Operation(summary = "Criar nova NFC-e", description = "Cria uma nova NFC-e com os dados fornecidos")
    public ResponseEntity<NfceResponseDto> criarNfce(
            @Valid @RequestBody NfceRequestDto request) {
        
        log.info("Recebida requisição para criar NFC-e - Série: {}, Número: {}", 
                request.getSerie(), request.getNumero());
        
        try {
            NfceResponseDto response = nfceService.criarNfce(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao criar NFC-e", e);
            throw e;
        } catch (Exception e) {
            log.error("Erro interno ao criar NFC-e", e);
            throw new RuntimeException("Erro interno do servidor", e);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar NFC-e por ID", description = "Retorna os dados de uma NFC-e pelo ID")
    public ResponseEntity<NfceResponseDto> buscarPorId(
            @Parameter(description = "ID da NFC-e") @PathVariable Long id) {
        
        log.info("Buscando NFC-e por ID: {}", id);
        
        try {
            NfceResponseDto response = nfceService.buscarPorId(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("NFC-e não encontrada: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/chave/{chaveAcesso}")
    @Operation(summary = "Buscar NFC-e por chave de acesso", description = "Retorna os dados de uma NFC-e pela chave de acesso")
    public ResponseEntity<NfceResponseDto> buscarPorChaveAcesso(
            @Parameter(description = "Chave de acesso da NFC-e") @PathVariable String chaveAcesso) {
        
        log.info("Buscando NFC-e por chave de acesso: {}", chaveAcesso);
        
        try {
            NfceResponseDto response = nfceService.buscarPorChaveAcesso(chaveAcesso);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("NFC-e não encontrada com chave: {}", chaveAcesso);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Buscar NFC-e por status", description = "Retorna lista de NFC-e filtradas por status")
    public ResponseEntity<List<NfceResponseDto>> buscarPorStatus(
            @Parameter(description = "Status da NFC-e (RASCUNHO, ASSINADA, ENVIADA, AUTORIZADA, etc.)") 
            @PathVariable String status) {
        
        log.info("Buscando NFC-e por status: {}", status);
        
        try {
            List<NfceResponseDto> response = nfceService.buscarPorStatus(status);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Status inválido: {}", status);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/emitente/{cnpj}")
    @Operation(summary = "Buscar NFC-e por CNPJ do emitente", description = "Retorna lista de NFC-e do emitente")
    public ResponseEntity<List<NfceResponseDto>> buscarPorEmitente(
            @Parameter(description = "CNPJ do emitente") @PathVariable String cnpj) {
        
        log.info("Buscando NFC-e por emitente: {}", cnpj);
        
        List<NfceResponseDto> response = nfceService.buscarPorEmitente(cnpj);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/assinar")
    @Operation(summary = "Assinar NFC-e", description = "Assina digitalmente uma NFC-e")
    public ResponseEntity<NfceResponseDto> assinarNfce(
            @Parameter(description = "ID da NFC-e") @PathVariable Long id) {
        
        log.info("Assinando NFC-e: {}", id);
        
        try {
            NfceResponseDto response = nfceService.assinarNfce(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("NFC-e não encontrada: {}", id);
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.error("Estado inválido para assinatura: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/enviar")
    @Operation(summary = "Enviar NFC-e", description = "Envia NFC-e para a SEFAZ")
    public ResponseEntity<NfceResponseDto> enviarNfce(
            @Parameter(description = "ID da NFC-e") @PathVariable Long id) {
        
        log.info("Enviando NFC-e: {}", id);
        
        try {
            NfceResponseDto response = nfceService.enviarNfce(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("NFC-e não encontrada: {}", id);
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.error("Estado inválido para envio: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/autorizar")
    @Operation(summary = "Autorizar NFC-e", description = "Simula a autorização de uma NFC-e pela SEFAZ")
    public ResponseEntity<NfceResponseDto> autorizarNfce(
            @Parameter(description = "ID da NFC-e") @PathVariable Long id) {
        
        log.info("Autorizando NFC-e: {}", id);
        
        try {
            NfceResponseDto response = nfceService.autorizarNfce(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("NFC-e não encontrada: {}", id);
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.error("Estado inválido para autorização: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar NFC-e", description = "Cancela uma NFC-e autorizada")
    public ResponseEntity<NfceResponseDto> cancelarNfce(
            @Parameter(description = "ID da NFC-e") @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String justificativa = request.get("justificativa");
        log.info("Cancelando NFC-e: {} - Justificativa: {}", id, justificativa);
        
        try {
            NfceResponseDto response = nfceService.cancelarNfce(id, justificativa);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Erro ao cancelar NFC-e: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            log.error("Estado inválido para cancelamento: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/numeracao/proxima/{serie}")
    @Operation(summary = "Obter próximo número", description = "Retorna o próximo número disponível para a série")
    public ResponseEntity<Map<String, Object>> obterProximoNumero(
            @Parameter(description = "Série da NFC-e") @PathVariable Integer serie) {
        
        log.info("Obtendo próximo número para série: {}", serie);
        
        Integer proximoNumero = nfceService.obterProximoNumero(serie);
        
        return ResponseEntity.ok(Map.of(
            "serie", serie,
            "proximo_numero", proximoNumero
        ));
    }
}
