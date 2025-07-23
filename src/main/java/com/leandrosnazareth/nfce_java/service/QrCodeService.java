package com.leandrosnazareth.nfce_java.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.leandrosnazareth.nfce_java.config.NfceConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class QrCodeService {

    private final NfceConfiguration nfceConfiguration;

    public String gerarUrlQrCode(String chaveAcesso, Integer ambiente, LocalDateTime dataEmissao,
                                BigDecimal valorTotal, String cpfCnpjDestinatario) {
        
        StringBuilder url = new StringBuilder();
        url.append(nfceConfiguration.getQrcode().getUrl().getConsulta());
        url.append("?p=");
        
        // Parâmetros do QR Code
        StringBuilder parametros = new StringBuilder();
        parametros.append(chaveAcesso);
        parametros.append("|");
        parametros.append(ambiente);
        parametros.append("|");
        parametros.append(dataEmissao.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        parametros.append("|");
        parametros.append(valorTotal.setScale(2).toString().replace(".", ""));
        parametros.append("|");
        
        // Hash do CPF/CNPJ do destinatário (se informado)
        if (cpfCnpjDestinatario != null && !cpfCnpjDestinatario.trim().isEmpty()) {
            String hashDestinatario = gerarHashSha1(cpfCnpjDestinatario);
            parametros.append(hashDestinatario);
        }
        
        // Codifica os parâmetros em Base64
        String parametrosBase64 = Base64.getEncoder()
            .encodeToString(parametros.toString().getBytes(StandardCharsets.UTF_8));
        
        url.append(parametrosBase64);
        
        return url.toString();
    }

    public byte[] gerarImagemQrCode(String conteudo, int largura, int altura) throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(conteudo, BarcodeFormat.QR_CODE, largura, altura, hints);

        BufferedImage image = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, largura, altura);
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < largura; i++) {
            for (int j = 0; j < altura; j++) {
                if (bitMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", outputStream);
        
        return outputStream.toByteArray();
    }

    public String gerarImagemQrCodeBase64(String conteudo, int largura, int altura) {
        try {
            byte[] imagemBytes = gerarImagemQrCode(conteudo, largura, altura);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imagemBytes);
        } catch (Exception e) {
            log.error("Erro ao gerar imagem do QR Code", e);
            return null;
        }
    }

    private String gerarHashSha1(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            log.error("Erro ao gerar hash SHA-1", e);
            return "";
        }
    }

    public boolean validarQrCode(String urlQrCode, String chaveAcesso) {
        try {
            if (urlQrCode == null || !urlQrCode.contains("?p=")) {
                return false;
            }
            
            String parametrosBase64 = urlQrCode.substring(urlQrCode.indexOf("?p=") + 3);
            String parametrosDecodificados = new String(
                Base64.getDecoder().decode(parametrosBase64), 
                StandardCharsets.UTF_8
            );
            
            String[] partes = parametrosDecodificados.split("\\|");
            
            return partes.length >= 4 && chaveAcesso.equals(partes[0]);
            
        } catch (Exception e) {
            log.error("Erro ao validar QR Code", e);
            return false;
        }
    }
}
