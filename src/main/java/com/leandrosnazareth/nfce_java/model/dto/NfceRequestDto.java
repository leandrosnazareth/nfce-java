package com.leandrosnazareth.nfce_java.model.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data
public class NfceRequestDto {

    @NotNull(message = "Número da nota é obrigatório")
    @Min(value = 1, message = "Número da nota deve ser maior que zero")
    private Integer numero;

    @NotNull(message = "Série da nota é obrigatória")
    @Min(value = 1, message = "Série deve ser maior que zero")
    private Integer serie;

    @NotBlank(message = "Natureza da operação é obrigatória")
    @Size(max = 60, message = "Natureza da operação deve ter no máximo 60 caracteres")
    @JsonProperty("natureza_operacao")
    private String naturezaOperacao;

    @Valid
    @NotNull(message = "Dados do emitente são obrigatórios")
    private EmitenteDto emitente;

    @Valid
    private DestinatarioDto destinatario;

    @Valid
    @NotEmpty(message = "Lista de itens não pode estar vazia")
    private List<ItemDto> itens;

    @Valid
    @NotEmpty(message = "Lista de pagamentos não pode estar vazia")
    private List<PagamentoDto> pagamentos;

    @Data
    public static class EmitenteDto {
        
        @NotBlank(message = "CNPJ do emitente é obrigatório")
        @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos")
        private String cnpj;

        @NotBlank(message = "Razão social é obrigatória")
        @Size(max = 60, message = "Razão social deve ter no máximo 60 caracteres")
        @JsonProperty("razao_social")
        private String razaoSocial;

        @Size(max = 60, message = "Nome fantasia deve ter no máximo 60 caracteres")
        @JsonProperty("nome_fantasia")
        private String nomeFantasia;

        @NotBlank(message = "Logradouro é obrigatório")
        @Size(max = 60, message = "Logradouro deve ter no máximo 60 caracteres")
        private String logradouro;

        @NotBlank(message = "Número é obrigatório")
        @Size(max = 60, message = "Número deve ter no máximo 60 caracteres")
        private String numero;

        @NotBlank(message = "Bairro é obrigatório")
        @Size(max = 60, message = "Bairro deve ter no máximo 60 caracteres")
        private String bairro;

        @NotBlank(message = "Município é obrigatório")
        @Size(max = 60, message = "Município deve ter no máximo 60 caracteres")
        private String municipio;

        @NotBlank(message = "UF é obrigatória")
        @Pattern(regexp = "[A-Z]{2}", message = "UF deve conter 2 letras maiúsculas")
        private String uf;

        @NotBlank(message = "CEP é obrigatório")
        @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos")
        private String cep;

        @NotNull(message = "Código do município é obrigatório")
        @JsonProperty("codigo_municipio")
        private Integer codigoMunicipio;

        @Pattern(regexp = "\\d{0,14}", message = "Inscrição estadual deve conter até 14 dígitos")
        @JsonProperty("inscricao_estadual")
        private String inscricaoEstadual;

        @NotNull(message = "Regime tributário é obrigatório")
        @JsonProperty("regime_tributario")
        private RegimeTributarioDto regimeTributario;
    }

    @Data
    public static class DestinatarioDto {
        
        @Pattern(regexp = "\\d{11,14}", message = "CPF/CNPJ deve conter 11 ou 14 dígitos")
        @JsonProperty("cpf_cnpj")
        private String cpfCnpj;

        @Size(max = 60, message = "Nome deve ter no máximo 60 caracteres")
        private String nome;
    }

    @Data
    public static class ItemDto {
        
        @NotNull(message = "Número do item é obrigatório")
        @Min(value = 1, message = "Número do item deve ser maior que zero")
        @JsonProperty("numero_item")
        private Integer numeroItem;

        @NotBlank(message = "Código do produto é obrigatório")
        @Size(max = 60, message = "Código do produto deve ter no máximo 60 caracteres")
        @JsonProperty("codigo_produto")
        private String codigoProduto;

        @NotBlank(message = "Descrição é obrigatória")
        @Size(max = 120, message = "Descrição deve ter no máximo 120 caracteres")
        private String descricao;

        @NotBlank(message = "NCM é obrigatório")
        @Pattern(regexp = "\\d{8}", message = "NCM deve conter 8 dígitos")
        private String ncm;

        @NotBlank(message = "CFOP é obrigatório")
        @Pattern(regexp = "\\d{4}", message = "CFOP deve conter 4 dígitos")
        private String cfop;

        @NotBlank(message = "Unidade comercial é obrigatória")
        @Size(max = 6, message = "Unidade comercial deve ter no máximo 6 caracteres")
        @JsonProperty("unidade_comercial")
        private String unidadeComercial;

        @NotNull(message = "Quantidade comercial é obrigatória")
        @DecimalMin(value = "0.0001", message = "Quantidade comercial deve ser maior que zero")
        @JsonProperty("quantidade_comercial")
        private BigDecimal quantidadeComercial;

        @NotNull(message = "Valor unitário comercial é obrigatório")
        @DecimalMin(value = "0.0000000001", message = "Valor unitário comercial deve ser maior que zero")
        @JsonProperty("valor_unitario_comercial")
        private BigDecimal valorUnitarioComercial;

        @DecimalMin(value = "0", message = "Valor de desconto deve ser maior ou igual a zero")
        @JsonProperty("valor_desconto")
        private BigDecimal valorDesconto;

        @NotNull(message = "Origem da mercadoria é obrigatória")
        @JsonProperty("origem_mercadoria")
        private OrigemMercadoriaDto origemMercadoria;

        @NotBlank(message = "CST ICMS é obrigatório")
        @Pattern(regexp = "\\d{3}", message = "CST ICMS deve conter 3 dígitos")
        @JsonProperty("cst_icms")
        private String cstIcms;

        @JsonProperty("modalidade_bc_icms")
        private Integer modalidadeBcIcms;

        @DecimalMin(value = "0", message = "Base de cálculo ICMS deve ser maior ou igual a zero")
        @JsonProperty("base_calculo_icms")
        private BigDecimal baseCalculoIcms;

        @DecimalMin(value = "0", message = "Alíquota ICMS deve ser maior ou igual a zero")
        @DecimalMax(value = "100", message = "Alíquota ICMS deve ser menor ou igual a 100")
        @JsonProperty("aliquota_icms")
        private BigDecimal aliquotaIcms;
    }

    @Data
    public static class PagamentoDto {
        
        @NotNull(message = "Meio de pagamento é obrigatório")
        @JsonProperty("meio_pagamento")
        private MeioPagamentoDto meioPagamento;

        @NotNull(message = "Valor do pagamento é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor do pagamento deve ser maior que zero")
        private BigDecimal valor;

        @Pattern(regexp = "\\d{14}", message = "CNPJ da credenciadora deve conter 14 dígitos")
        @JsonProperty("cnpj_credenciadora")
        private String cnpjCredenciadora;

        @Pattern(regexp = "\\d{2}", message = "Bandeira da operadora deve conter 2 dígitos")
        @JsonProperty("bandeira_operadora")
        private String bandeiraOperadora;

        @Size(max = 20, message = "Número de autorização deve ter no máximo 20 caracteres")
        @JsonProperty("numero_autorizacao")
        private String numeroAutorizacao;
    }

    public enum RegimeTributarioDto {
        SIMPLES_NACIONAL, SIMPLES_NACIONAL_EXCESSO_SUBLIMITE, REGIME_NORMAL
    }

    public enum OrigemMercadoriaDto {
        NACIONAL, ESTRANGEIRA_IMPORTACAO_DIRETA, ESTRANGEIRA_ADQUIRIDA_MERCADO_INTERNO,
        NACIONAL_CONTEUDO_IMPORTACAO_SUPERIOR_40, NACIONAL_CIDE, NACIONAL_CONTEUDO_IMPORTACAO_INFERIOR_40,
        ESTRANGEIRA_IMPORTACAO_DIRETA_CAMEX, ESTRANGEIRA_ADQUIRIDA_MERCADO_INTERNO_CAMEX,
        NACIONAL_CONTEUDO_IMPORTACAO_SUPERIOR_70
    }

    public enum MeioPagamentoDto {
        DINHEIRO, CHEQUE, CARTAO_CREDITO, CARTAO_DEBITO, CREDITO_LOJA, VALE_ALIMENTACAO,
        VALE_REFEICAO, VALE_PRESENTE, VALE_COMBUSTIVEL, DUPLICATA_MERCANTIL, BOLETO_BANCARIO,
        DEPOSITO_BANCARIO, PIX, TRANSFERENCIA_BANCARIA, PROGRAMA_FIDELIDADE, SEM_PAGAMENTO, OUTROS
    }
}
