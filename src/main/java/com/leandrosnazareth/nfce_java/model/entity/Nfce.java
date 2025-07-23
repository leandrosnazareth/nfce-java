package com.leandrosnazareth.nfce_java.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "nfce")
@Data
@EqualsAndHashCode(exclude = {"itens", "pagamentos"})
public class Nfce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "serie", nullable = false)
    private Integer serie;

    @Column(name = "chave_acesso", unique = true, length = 44)
    private String chaveAcesso;

    @Column(name = "data_emissao", nullable = false)
    private LocalDateTime dataEmissao;

    @Column(name = "natureza_operacao", nullable = false, length = 60)
    private String naturezaOperacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_operacao", nullable = false)
    private TipoOperacao tipoOperacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "finalidade_emissao", nullable = false)
    private FinalidadeEmissao finalidadeEmissao;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "ambiente", nullable = false)
    private Ambiente ambiente;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusNfce status;

    @Column(name = "protocolo_autorizacao", length = 15)
    private String protocoloAutorizacao;

    @Column(name = "data_autorizacao")
    private LocalDateTime dataAutorizacao;

    @Column(name = "xml_assinado", columnDefinition = "TEXT")
    private String xmlAssinado;

    @Column(name = "xml_autorizado", columnDefinition = "TEXT")
    private String xmlAutorizado;

    @Column(name = "qr_code", columnDefinition = "TEXT")
    private String qrCode;

    @Column(name = "url_consulta", length = 500)
    private String urlConsulta;

    // Emitente
    @Column(name = "emitente_cnpj", nullable = false, length = 14)
    private String emitenteCnpj;

    @Column(name = "emitente_razao_social", nullable = false, length = 60)
    private String emitenteRazaoSocial;

    @Column(name = "emitente_nome_fantasia", length = 60)
    private String emitenteNomeFantasia;

    @Column(name = "emitente_logradouro", nullable = false, length = 60)
    private String emitenteLogradouro;

    @Column(name = "emitente_numero", nullable = false, length = 60)
    private String emitenteNumero;

    @Column(name = "emitente_bairro", nullable = false, length = 60)
    private String emitenteBairro;

    @Column(name = "emitente_municipio", nullable = false, length = 60)
    private String emitenteMunicipio;

    @Column(name = "emitente_uf", nullable = false, length = 2)
    private String emitenteUf;

    @Column(name = "emitente_cep", nullable = false, length = 8)
    private String emitenteCep;

    @Column(name = "emitente_codigo_municipio", nullable = false)
    private Integer emitenteCodigoMunicipio;

    @Column(name = "emitente_inscricao_estadual", length = 14)
    private String emitenteInscricaoEstadual;

    @Enumerated(EnumType.STRING)
    @Column(name = "emitente_regime_tributario", nullable = false)
    private RegimeTributario emitenteRegimeTributario;

    // Destinatário (opcional para NFC-e)
    @Column(name = "destinatario_cpf_cnpj", length = 14)
    private String destinatarioCpfCnpj;

    @Column(name = "destinatario_nome", length = 60)
    private String destinatarioNome;

    // Totais
    @Column(name = "valor_total_produtos", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalProdutos;

    @Column(name = "valor_total_nota", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalNota;

    @Column(name = "valor_desconto", precision = 15, scale = 2)
    private BigDecimal valorDesconto;

    @Column(name = "valor_frete", precision = 15, scale = 2)
    private BigDecimal valorFrete;

    @Column(name = "valor_seguro", precision = 15, scale = 2)
    private BigDecimal valorSeguro;

    @Column(name = "outras_despesas", precision = 15, scale = 2)
    private BigDecimal outrasDespesas;

    // ICMS
    @Column(name = "base_calculo_icms", precision = 15, scale = 2)
    private BigDecimal baseCalculoIcms;

    @Column(name = "valor_icms", precision = 15, scale = 2)
    private BigDecimal valorIcms;

    @Column(name = "base_calculo_icms_st", precision = 15, scale = 2)
    private BigDecimal baseCalculoIcmsSt;

    @Column(name = "valor_icms_st", precision = 15, scale = 2)
    private BigDecimal valorIcmsSt;

    // PIS/COFINS
    @Column(name = "valor_pis", precision = 15, scale = 2)
    private BigDecimal valorPis;

    @Column(name = "valor_cofins", precision = 15, scale = 2)
    private BigDecimal valorCofins;

    @OneToMany(mappedBy = "nfce", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemNfce> itens = new ArrayList<>();

    @OneToMany(mappedBy = "nfce", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PagamentoNfce> pagamentos = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = StatusNfce.RASCUNHO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum TipoOperacao {
        ENTRADA(0), SAIDA(1);
        
        private final int codigo;
        
        TipoOperacao(int codigo) {
            this.codigo = codigo;
        }
        
        public int getCodigo() {
            return codigo;
        }
    }

    public enum FinalidadeEmissao {
        NORMAL(1), COMPLEMENTAR(2), AJUSTE(3), DEVOLUCAO(4);
        
        private final int codigo;
        
        FinalidadeEmissao(int codigo) {
            this.codigo = codigo;
        }
        
        public int getCodigo() {
            return codigo;
        }
    }

    public enum FormaPagamento {
        PAGAMENTO_A_VISTA(0), PAGAMENTO_A_PRAZO(1), OUTROS(2);
        
        private final int codigo;
        
        FormaPagamento(int codigo) {
            this.codigo = codigo;
        }
        
        public int getCodigo() {
            return codigo;
        }
    }

    public enum Ambiente {
        PRODUCAO(1), HOMOLOGACAO(2);
        
        private final int codigo;
        
        Ambiente(int codigo) {
            this.codigo = codigo;
        }
        
        public int getCodigo() {
            return codigo;
        }
    }

    public enum StatusNfce {
        RASCUNHO, ASSINADA, ENVIADA, AUTORIZADA, REJEITADA, CANCELADA, DENEGADA
    }

    public enum RegimeTributario {
        SIMPLES_NACIONAL(1),
        SIMPLES_NACIONAL_EXCESSO_SUBLIMITE(2),
        REGIME_NORMAL(3);
        
        private final int codigo;
        
        RegimeTributario(int codigo) {
            this.codigo = codigo;
        }
        
        public int getCodigo() {
            return codigo;
        }
    }
}
