package com.leandrosnazareth.nfce_java.model.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class NfceResponseDto {

    private Long id;
    private Integer numero;
    private Integer serie;
    
    @JsonProperty("chave_acesso")
    private String chaveAcesso;
    
    @JsonProperty("data_emissao")
    private LocalDateTime dataEmissao;
    
    @JsonProperty("natureza_operacao")
    private String naturezaOperacao;
    
    private String status;
    
    @JsonProperty("protocolo_autorizacao")
    private String protocoloAutorizacao;
    
    @JsonProperty("data_autorizacao")
    private LocalDateTime dataAutorizacao;
    
    @JsonProperty("qr_code")
    private String qrCode;
    
    @JsonProperty("url_consulta")
    private String urlConsulta;
    
    private EmitenteResponseDto emitente;
    private DestinatarioResponseDto destinatario;
    private List<ItemResponseDto> itens;
    private List<PagamentoResponseDto> pagamentos;
    private TotaisResponseDto totais;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @Data
    public static class EmitenteResponseDto {
        private String cnpj;
        
        @JsonProperty("razao_social")
        private String razaoSocial;
        
        @JsonProperty("nome_fantasia")
        private String nomeFantasia;
        
        private String logradouro;
        private String numero;
        private String bairro;
        private String municipio;
        private String uf;
        private String cep;
        
        @JsonProperty("codigo_municipio")
        private Integer codigoMunicipio;
        
        @JsonProperty("inscricao_estadual")
        private String inscricaoEstadual;
        
        @JsonProperty("regime_tributario")
        private String regimeTributario;
    }

    @Data
    public static class DestinatarioResponseDto {
        
        @JsonProperty("cpf_cnpj")
        private String cpfCnpj;
        
        private String nome;
    }

    @Data
    public static class ItemResponseDto {
        
        @JsonProperty("numero_item")
        private Integer numeroItem;
        
        @JsonProperty("codigo_produto")
        private String codigoProduto;
        
        private String descricao;
        private String ncm;
        private String cfop;
        
        @JsonProperty("unidade_comercial")
        private String unidadeComercial;
        
        @JsonProperty("quantidade_comercial")
        private BigDecimal quantidadeComercial;
        
        @JsonProperty("valor_unitario_comercial")
        private BigDecimal valorUnitarioComercial;
        
        @JsonProperty("valor_total_bruto")
        private BigDecimal valorTotalBruto;
        
        @JsonProperty("valor_desconto")
        private BigDecimal valorDesconto;
        
        @JsonProperty("origem_mercadoria")
        private String origemMercadoria;
        
        @JsonProperty("cst_icms")
        private String cstIcms;
        
        @JsonProperty("base_calculo_icms")
        private BigDecimal baseCalculoIcms;
        
        @JsonProperty("aliquota_icms")
        private BigDecimal aliquotaIcms;
        
        @JsonProperty("valor_icms")
        private BigDecimal valorIcms;
    }

    @Data
    public static class PagamentoResponseDto {
        
        @JsonProperty("meio_pagamento")
        private String meioPagamento;
        
        private BigDecimal valor;
        
        @JsonProperty("cnpj_credenciadora")
        private String cnpjCredenciadora;
        
        @JsonProperty("bandeira_operadora")
        private String bandeiraOperadora;
        
        @JsonProperty("numero_autorizacao")
        private String numeroAutorizacao;
    }

    @Data
    public static class TotaisResponseDto {
        
        @JsonProperty("valor_total_produtos")
        private BigDecimal valorTotalProdutos;
        
        @JsonProperty("valor_total_nota")
        private BigDecimal valorTotalNota;
        
        @JsonProperty("valor_desconto")
        private BigDecimal valorDesconto;
        
        @JsonProperty("base_calculo_icms")
        private BigDecimal baseCalculoIcms;
        
        @JsonProperty("valor_icms")
        private BigDecimal valorIcms;
        
        @JsonProperty("valor_pis")
        private BigDecimal valorPis;
        
        @JsonProperty("valor_cofins")
        private BigDecimal valorCofins;
    }
}
