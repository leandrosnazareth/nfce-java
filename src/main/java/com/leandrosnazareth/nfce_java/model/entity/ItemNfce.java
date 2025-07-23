package com.leandrosnazareth.nfce_java.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "item_nfce")
@Data
@EqualsAndHashCode(exclude = {"nfce"})
public class ItemNfce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nfce_id", nullable = false)
    private Nfce nfce;

    @Column(name = "numero_item", nullable = false)
    private Integer numeroItem;

    @Column(name = "codigo_produto", nullable = false, length = 60)
    private String codigoProduto;

    @Column(name = "descricao", nullable = false, length = 120)
    private String descricao;

    @Column(name = "ncm", nullable = false, length = 8)
    private String ncm;

    @Column(name = "cfop", nullable = false, length = 4)
    private String cfop;

    @Column(name = "unidade_comercial", nullable = false, length = 6)
    private String unidadeComercial;

    @Column(name = "quantidade_comercial", nullable = false, precision = 15, scale = 4)
    private BigDecimal quantidadeComercial;

    @Column(name = "valor_unitario_comercial", nullable = false, precision = 15, scale = 10)
    private BigDecimal valorUnitarioComercial;

    @Column(name = "valor_total_bruto", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalBruto;

    @Column(name = "unidade_tributavel", nullable = false, length = 6)
    private String unidadeTributavel;

    @Column(name = "quantidade_tributavel", nullable = false, precision = 15, scale = 4)
    private BigDecimal quantidadeTributavel;

    @Column(name = "valor_unitario_tributavel", nullable = false, precision = 15, scale = 10)
    private BigDecimal valorUnitarioTributavel;

    @Column(name = "valor_desconto", precision = 15, scale = 2)
    private BigDecimal valorDesconto;

    @Column(name = "valor_frete", precision = 15, scale = 2)
    private BigDecimal valorFrete;

    @Column(name = "valor_seguro", precision = 15, scale = 2)
    private BigDecimal valorSeguro;

    @Column(name = "outras_despesas", precision = 15, scale = 2)
    private BigDecimal outrasDespesas;

    @Enumerated(EnumType.STRING)
    @Column(name = "inclui_no_total", nullable = false)
    private IncluiNoTotal incluiNoTotal;

    // ICMS
    @Enumerated(EnumType.STRING)
    @Column(name = "origem_mercadoria", nullable = false)
    private OrigemMercadoria origemMercadoria;

    @Column(name = "cst_icms", nullable = false, length = 3)
    private String cstIcms;

    @Column(name = "modalidade_bc_icms")
    private Integer modalidadeBcIcms;

    @Column(name = "base_calculo_icms", precision = 15, scale = 2)
    private BigDecimal baseCalculoIcms;

    @Column(name = "aliquota_icms", precision = 5, scale = 2)
    private BigDecimal aliquotaIcms;

    @Column(name = "valor_icms", precision = 15, scale = 2)
    private BigDecimal valorIcms;

    // ICMS ST
    @Column(name = "modalidade_bc_icms_st")
    private Integer modalidadeBcIcmsSt;

    @Column(name = "base_calculo_icms_st", precision = 15, scale = 2)
    private BigDecimal baseCalculoIcmsSt;

    @Column(name = "aliquota_icms_st", precision = 5, scale = 2)
    private BigDecimal aliquotaIcmsSt;

    @Column(name = "valor_icms_st", precision = 15, scale = 2)
    private BigDecimal valorIcmsSt;

    // PIS
    @Column(name = "cst_pis", length = 2)
    private String cstPis;

    @Column(name = "base_calculo_pis", precision = 15, scale = 2)
    private BigDecimal baseCalculoPis;

    @Column(name = "aliquota_pis", precision = 5, scale = 4)
    private BigDecimal aliquotaPis;

    @Column(name = "valor_pis", precision = 15, scale = 2)
    private BigDecimal valorPis;

    // COFINS
    @Column(name = "cst_cofins", length = 2)
    private String cstCofins;

    @Column(name = "base_calculo_cofins", precision = 15, scale = 2)
    private BigDecimal baseCalculoCofins;

    @Column(name = "aliquota_cofins", precision = 5, scale = 4)
    private BigDecimal aliquotaCofins;

    @Column(name = "valor_cofins", precision = 15, scale = 2)
    private BigDecimal valorCofins;

    public enum IncluiNoTotal {
        SIM(1), NAO(0);
        
        private final int codigo;
        
        IncluiNoTotal(int codigo) {
            this.codigo = codigo;
        }
        
        public int getCodigo() {
            return codigo;
        }
    }

    public enum OrigemMercadoria {
        NACIONAL(0),
        ESTRANGEIRA_IMPORTACAO_DIRETA(1),
        ESTRANGEIRA_ADQUIRIDA_MERCADO_INTERNO(2),
        NACIONAL_CONTEUDO_IMPORTACAO_SUPERIOR_40(3),
        NACIONAL_CIDE(4),
        NACIONAL_CONTEUDO_IMPORTACAO_INFERIOR_40(5),
        ESTRANGEIRA_IMPORTACAO_DIRETA_CAMEX(6),
        ESTRANGEIRA_ADQUIRIDA_MERCADO_INTERNO_CAMEX(7),
        NACIONAL_CONTEUDO_IMPORTACAO_SUPERIOR_70(8);
        
        private final int codigo;
        
        OrigemMercadoria(int codigo) {
            this.codigo = codigo;
        }
        
        public int getCodigo() {
            return codigo;
        }
    }
}
