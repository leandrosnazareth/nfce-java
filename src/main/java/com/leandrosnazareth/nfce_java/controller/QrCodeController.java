package com.leandrosnazareth.nfce_java.controller;

import com.leandrosnazareth.nfce_java.service.QrCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/qrcode")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "QR Code", description = "APIs para geração e validação de QR Code da NFC-e")
public class QrCodeController {

    private final QrCodeService qrCodeService;

    @GetMapping("/gerar")
    @Operation(summary = "Gerar URL do QR Code", description = "Gera a URL do QR Code para consulta da NFC-e")
    public ResponseEntity<Map<String, String>> gerarUrlQrCode(
            @Parameter(description = "Chave de acesso da NFC-e") @RequestParam String chaveAcesso,
            @Parameter(description = "Ambiente (1=Produção, 2=Homologação)") @RequestParam Integer ambiente,
            @Parameter(description = "Data de emissão") @RequestParam String dataEmissao,
            @Parameter(description = "Valor total da nota") @RequestParam BigDecimal valorTotal,
            @Parameter(description = "CPF/CNPJ do destinatário") @RequestParam(required = false) String cpfCnpjDestinatario) {
        
        log.info("Gerando URL do QR Code para chave: {}", chaveAcesso);
        
        try {
            LocalDateTime dataEmissaoFormatada = LocalDateTime.parse(dataEmissao);
            
            String urlQrCode = qrCodeService.gerarUrlQrCode(
                chaveAcesso, 
                ambiente, 
                dataEmissaoFormatada, 
                valorTotal, 
                cpfCnpjDestinatario
            );
            
            return ResponseEntity.ok(Map.of(
                "url_qrcode", urlQrCode,
                "chave_acesso", chaveAcesso
            ));
            
        } catch (Exception e) {
            log.error("Erro ao gerar URL do QR Code", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/imagem")
    @Operation(summary = "Gerar imagem do QR Code", description = "Gera a imagem PNG do QR Code")
    public ResponseEntity<byte[]> gerarImagemQrCode(
            @Parameter(description = "Conteúdo do QR Code (URL)") @RequestParam String conteudo,
            @Parameter(description = "Largura da imagem") @RequestParam(defaultValue = "300") int largura,
            @Parameter(description = "Altura da imagem") @RequestParam(defaultValue = "300") int altura) {
        
        log.info("Gerando imagem do QR Code com dimensões: {}x{}", largura, altura);
        
        try {
            byte[] imagemBytes = qrCodeService.gerarImagemQrCode(conteudo, largura, altura);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(imagemBytes.length);
            
            return new ResponseEntity<>(imagemBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Erro ao gerar imagem do QR Code", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/imagem-base64")
    @Operation(summary = "Gerar imagem do QR Code em Base64", description = "Gera a imagem do QR Code codificada em Base64")
    public ResponseEntity<Map<String, String>> gerarImagemQrCodeBase64(
            @Parameter(description = "Conteúdo do QR Code (URL)") @RequestParam String conteudo,
            @Parameter(description = "Largura da imagem") @RequestParam(defaultValue = "300") int largura,
            @Parameter(description = "Altura da imagem") @RequestParam(defaultValue = "300") int altura) {
        
        log.info("Gerando imagem Base64 do QR Code com dimensões: {}x{}", largura, altura);
        
        try {
            String imagemBase64 = qrCodeService.gerarImagemQrCodeBase64(conteudo, largura, altura);
            
            if (imagemBase64 != null) {
                return ResponseEntity.ok(Map.of(
                    "imagem_base64", imagemBase64,
                    "largura", String.valueOf(largura),
                    "altura", String.valueOf(altura)
                ));
            } else {
                return ResponseEntity.internalServerError().build();
            }
            
        } catch (Exception e) {
            log.error("Erro ao gerar imagem Base64 do QR Code", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/validar")
    @Operation(summary = "Validar QR Code", description = "Valida se o QR Code está corretamente formatado")
    public ResponseEntity<Map<String, Object>> validarQrCode(
            @RequestBody Map<String, String> request) {
        
        String urlQrCode = request.get("url_qrcode");
        String chaveAcesso = request.get("chave_acesso");
        
        log.info("Validando QR Code para chave: {}", chaveAcesso);
        
        try {
            boolean isValido = qrCodeService.validarQrCode(urlQrCode, chaveAcesso);
            
            return ResponseEntity.ok(Map.of(
                "valido", isValido,
                "chave_acesso", chaveAcesso,
                "url_qrcode", urlQrCode
            ));
            
        } catch (Exception e) {
            log.error("Erro ao validar QR Code", e);
            return ResponseEntity.ok(Map.of(
                "valido", false,
                "erro", "Erro na validação: " + e.getMessage()
            ));
        }
    }
}
