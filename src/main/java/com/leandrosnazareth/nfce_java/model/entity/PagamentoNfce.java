package com.leandrosnazareth.nfce_java.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pagamento_nfce")
@Data
@EqualsAndHashCode(exclude = {"nfce"})
public class PagamentoNfce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nfce_id", nullable = false)
    private Nfce nfce;

    @Enumerated(EnumType.STRING)
    @Column(name = "meio_pagamento", nullable = false)
    private MeioPagamento meioPagamento;

    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "cnpj_credenciadora", length = 14)
    private String cnpjCredenciadora;

    @Column(name = "bandeira_operadora", length = 2)
    private String bandeiraOperadora;

    @Column(name = "numero_autorizacao", length = 20)
    private String numeroAutorizacao;

    public enum MeioPagamento {
        DINHEIRO("01"),
        CHEQUE("02"),
        CARTAO_CREDITO("03"),
        CARTAO_DEBITO("04"),
        CREDITO_LOJA("05"),
        VALE_ALIMENTACAO("10"),
        VALE_REFEICAO("11"),
        VALE_PRESENTE("12"),
        VALE_COMBUSTIVEL("13"),
        DUPLICATA_MERCANTIL("14"),
        BOLETO_BANCARIO("15"),
        DEPOSITO_BANCARIO("16"),
        PIX("17"),
        TRANSFERENCIA_BANCARIA("18"),
        PROGRAMA_FIDELIDADE("19"),
        SEM_PAGAMENTO("90"),
        OUTROS("99");

        private final String codigo;

        MeioPagamento(String codigo) {
            this.codigo = codigo;
        }

        public String getCodigo() {
            return codigo;
        }
    }
}
