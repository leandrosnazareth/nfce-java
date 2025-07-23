package com.leandrosnazareth.nfce_java.service;

import com.leandrosnazareth.nfce_java.model.entity.Nfce;
import com.leandrosnazareth.nfce_java.model.entity.ItemNfce;
import com.leandrosnazareth.nfce_java.model.entity.PagamentoNfce;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class XmlService {

    private static final String NAMESPACE_NFE = "http://www.portalfiscal.inf.br/nfe";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String gerarXmlNfce(Nfce nfce) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            // Elemento raiz
            Element nfeProc = document.createElementNS(NAMESPACE_NFE, "nfeProc");
            nfeProc.setAttribute("versao", "4.00");
            nfeProc.setAttribute("xmlns", NAMESPACE_NFE);
            document.appendChild(nfeProc);

            // NFe
            Element nfeElement = document.createElement("NFe");
            nfeProc.appendChild(nfeElement);

            // infNFe
            Element infNFe = document.createElement("infNFe");
            infNFe.setAttribute("Id", "NFe" + nfce.getChaveAcesso());
            infNFe.setAttribute("versao", "4.00");
            nfeElement.appendChild(infNFe);

            // ide - Identificação
            criarElementoIde(document, infNFe, nfce);

            // emit - Emitente
            criarElementoEmit(document, infNFe, nfce);

            // dest - Destinatário (opcional para NFC-e)
            if (nfce.getDestinatarioCpfCnpj() != null && !nfce.getDestinatarioCpfCnpj().trim().isEmpty()) {
                criarElementoDest(document, infNFe, nfce);
            }

            // det - Detalhamento dos produtos/serviços
            for (ItemNfce item : nfce.getItens()) {
                criarElementoDet(document, infNFe, item);
            }

            // total - Totais
            criarElementoTotal(document, infNFe, nfce);

            // transp - Transporte
            criarElementoTransp(document, infNFe);

            // pag - Pagamento
            criarElementoPag(document, infNFe, nfce);

            // infAdic - Informações adicionais
            criarElementoInfAdic(document, infNFe, nfce);

            return documentToString(document);

        } catch (Exception e) {
            log.error("Erro ao gerar XML da NFC-e", e);
            throw new RuntimeException("Erro ao gerar XML da NFC-e", e);
        }
    }

    private void criarElementoIde(Document document, Element parent, Nfce nfce) {
        Element ide = document.createElement("ide");
        parent.appendChild(ide);

        addElement(document, ide, "cUF", nfce.getEmitenteUf());
        addElement(document, ide, "cNF", nfce.getChaveAcesso().substring(35, 43));
        addElement(document, ide, "natOp", nfce.getNaturezaOperacao());
        addElement(document, ide, "mod", "65");
        addElement(document, ide, "serie", nfce.getSerie().toString());
        addElement(document, ide, "nNF", nfce.getNumero().toString());
        addElement(document, ide, "dhEmi", nfce.getDataEmissao().format(DATE_TIME_FORMATTER));
        addElement(document, ide, "tpNF", String.valueOf(nfce.getTipoOperacao().getCodigo()));
        addElement(document, ide, "idDest", "1"); // Operação interna
        addElement(document, ide, "cMunFG", nfce.getEmitenteCodigoMunicipio().toString());
        addElement(document, ide, "tpImp", "4"); // DANFE NFC-e
        addElement(document, ide, "tpEmis", "1"); // Emissão normal
        addElement(document, ide, "cDV", nfce.getChaveAcesso().substring(43));
        addElement(document, ide, "tpAmb", String.valueOf(nfce.getAmbiente().getCodigo()));
        addElement(document, ide, "finNFe", String.valueOf(nfce.getFinalidadeEmissao().getCodigo()));
        addElement(document, ide, "indFinal", "1"); // Consumidor final
        addElement(document, ide, "indPres", "1"); // Operação presencial
        addElement(document, ide, "indIntermed", "0"); // Sem intermediador
    }

    private void criarElementoEmit(Document document, Element parent, Nfce nfce) {
        Element emit = document.createElement("emit");
        parent.appendChild(emit);

        addElement(document, emit, "CNPJ", nfce.getEmitenteCnpj());
        addElement(document, emit, "xNome", nfce.getEmitenteRazaoSocial());
        
        if (nfce.getEmitenteNomeFantasia() != null) {
            addElement(document, emit, "xFant", nfce.getEmitenteNomeFantasia());
        }

        Element enderEmit = document.createElement("enderEmit");
        emit.appendChild(enderEmit);
        
        addElement(document, enderEmit, "xLgr", nfce.getEmitenteLogradouro());
        addElement(document, enderEmit, "nro", nfce.getEmitenteNumero());
        addElement(document, enderEmit, "xBairro", nfce.getEmitenteBairro());
        addElement(document, enderEmit, "cMun", nfce.getEmitenteCodigoMunicipio().toString());
        addElement(document, enderEmit, "xMun", nfce.getEmitenteMunicipio());
        addElement(document, enderEmit, "UF", nfce.getEmitenteUf());
        addElement(document, enderEmit, "CEP", nfce.getEmitenteCep());

        if (nfce.getEmitenteInscricaoEstadual() != null) {
            addElement(document, emit, "IE", nfce.getEmitenteInscricaoEstadual());
        }
        
        addElement(document, emit, "CRT", String.valueOf(nfce.getEmitenteRegimeTributario().getCodigo()));
    }

    private void criarElementoDest(Document document, Element parent, Nfce nfce) {
        Element dest = document.createElement("dest");
        parent.appendChild(dest);

        if (nfce.getDestinatarioCpfCnpj().length() == 11) {
            addElement(document, dest, "CPF", nfce.getDestinatarioCpfCnpj());
        } else {
            addElement(document, dest, "CNPJ", nfce.getDestinatarioCpfCnpj());
        }

        if (nfce.getDestinatarioNome() != null) {
            addElement(document, dest, "xNome", nfce.getDestinatarioNome());
        }

        addElement(document, dest, "indIEDest", "9"); // Não contribuinte
    }

    private void criarElementoDet(Document document, Element parent, ItemNfce item) {
        Element det = document.createElement("det");
        det.setAttribute("nItem", item.getNumeroItem().toString());
        parent.appendChild(det);

        // prod - Produto
        Element prod = document.createElement("prod");
        det.appendChild(prod);

        addElement(document, prod, "cProd", item.getCodigoProduto());
        addElement(document, prod, "cEAN", "");
        addElement(document, prod, "xProd", item.getDescricao());
        addElement(document, prod, "NCM", item.getNcm());
        addElement(document, prod, "CFOP", item.getCfop());
        addElement(document, prod, "uCom", item.getUnidadeComercial());
        addElement(document, prod, "qCom", formatDecimal(item.getQuantidadeComercial(), 4));
        addElement(document, prod, "vUnCom", formatDecimal(item.getValorUnitarioComercial(), 10));
        addElement(document, prod, "vProd", formatDecimal(item.getValorTotalBruto(), 2));
        addElement(document, prod, "cEANTrib", "");
        addElement(document, prod, "uTrib", item.getUnidadeTributavel());
        addElement(document, prod, "qTrib", formatDecimal(item.getQuantidadeTributavel(), 4));
        addElement(document, prod, "vUnTrib", formatDecimal(item.getValorUnitarioTributavel(), 10));

        if (item.getValorDesconto() != null && item.getValorDesconto().compareTo(BigDecimal.ZERO) > 0) {
            addElement(document, prod, "vDesc", formatDecimal(item.getValorDesconto(), 2));
        }

        addElement(document, prod, "indTot", String.valueOf(item.getIncluiNoTotal().getCodigo()));

        // imposto - Impostos
        Element imposto = document.createElement("imposto");
        det.appendChild(imposto);

        // ICMS
        criarElementoIcms(document, imposto, item);

        // PIS
        if (item.getCstPis() != null) {
            criarElementoPis(document, imposto, item);
        }

        // COFINS
        if (item.getCstCofins() != null) {
            criarElementoCofins(document, imposto, item);
        }
    }

    private void criarElementoIcms(Document document, Element parent, ItemNfce item) {
        Element icms = document.createElement("ICMS");
        parent.appendChild(icms);

        String cstIcms = item.getCstIcms();
        Element icmsElement = document.createElement("ICMS" + cstIcms);
        icms.appendChild(icmsElement);

        addElement(document, icmsElement, "orig", String.valueOf(item.getOrigemMercadoria().getCodigo()));
        addElement(document, icmsElement, "CST", cstIcms);

        if (item.getModalidadeBcIcms() != null) {
            addElement(document, icmsElement, "modBC", item.getModalidadeBcIcms().toString());
        }

        if (item.getBaseCalculoIcms() != null) {
            addElement(document, icmsElement, "vBC", formatDecimal(item.getBaseCalculoIcms(), 2));
        }

        if (item.getAliquotaIcms() != null) {
            addElement(document, icmsElement, "pICMS", formatDecimal(item.getAliquotaIcms(), 2));
        }

        if (item.getValorIcms() != null) {
            addElement(document, icmsElement, "vICMS", formatDecimal(item.getValorIcms(), 2));
        }
    }

    private void criarElementoPis(Document document, Element parent, ItemNfce item) {
        Element pis = document.createElement("PIS");
        parent.appendChild(pis);

        String cstPis = item.getCstPis();
        Element pisElement = document.createElement("PIS" + cstPis);
        pis.appendChild(pisElement);

        addElement(document, pisElement, "CST", cstPis);

        if (item.getBaseCalculoPis() != null) {
            addElement(document, pisElement, "vBC", formatDecimal(item.getBaseCalculoPis(), 2));
            addElement(document, pisElement, "pPIS", formatDecimal(item.getAliquotaPis(), 4));
            addElement(document, pisElement, "vPIS", formatDecimal(item.getValorPis(), 2));
        }
    }

    private void criarElementoCofins(Document document, Element parent, ItemNfce item) {
        Element cofins = document.createElement("COFINS");
        parent.appendChild(cofins);

        String cstCofins = item.getCstCofins();
        Element cofinsElement = document.createElement("COFINS" + cstCofins);
        cofins.appendChild(cofinsElement);

        addElement(document, cofinsElement, "CST", cstCofins);

        if (item.getBaseCalculoCofins() != null) {
            addElement(document, cofinsElement, "vBC", formatDecimal(item.getBaseCalculoCofins(), 2));
            addElement(document, cofinsElement, "pCOFINS", formatDecimal(item.getAliquotaCofins(), 4));
            addElement(document, cofinsElement, "vCOFINS", formatDecimal(item.getValorCofins(), 2));
        }
    }

    private void criarElementoTotal(Document document, Element parent, Nfce nfce) {
        Element total = document.createElement("total");
        parent.appendChild(total);

        Element icmsTot = document.createElement("ICMSTot");
        total.appendChild(icmsTot);

        addElement(document, icmsTot, "vBC", formatDecimal(nfce.getBaseCalculoIcms(), 2));
        addElement(document, icmsTot, "vICMS", formatDecimal(nfce.getValorIcms(), 2));
        addElement(document, icmsTot, "vICMSDeson", "0.00");
        addElement(document, icmsTot, "vFCP", "0.00");
        addElement(document, icmsTot, "vBCST", formatDecimal(nfce.getBaseCalculoIcmsSt(), 2));
        addElement(document, icmsTot, "vST", formatDecimal(nfce.getValorIcmsSt(), 2));
        addElement(document, icmsTot, "vFCPST", "0.00");
        addElement(document, icmsTot, "vFCPSTRet", "0.00");
        addElement(document, icmsTot, "vProd", formatDecimal(nfce.getValorTotalProdutos(), 2));
        addElement(document, icmsTot, "vFrete", formatDecimal(nfce.getValorFrete(), 2));
        addElement(document, icmsTot, "vSeg", formatDecimal(nfce.getValorSeguro(), 2));
        addElement(document, icmsTot, "vDesc", formatDecimal(nfce.getValorDesconto(), 2));
        addElement(document, icmsTot, "vII", "0.00");
        addElement(document, icmsTot, "vIPI", "0.00");
        addElement(document, icmsTot, "vIPIDevol", "0.00");
        addElement(document, icmsTot, "vPIS", formatDecimal(nfce.getValorPis(), 2));
        addElement(document, icmsTot, "vCOFINS", formatDecimal(nfce.getValorCofins(), 2));
        addElement(document, icmsTot, "vOutro", formatDecimal(nfce.getOutrasDespesas(), 2));
        addElement(document, icmsTot, "vNF", formatDecimal(nfce.getValorTotalNota(), 2));
    }

    private void criarElementoTransp(Document document, Element parent) {
        Element transp = document.createElement("transp");
        parent.appendChild(transp);

        addElement(document, transp, "modFrete", "9"); // Sem frete
    }

    private void criarElementoPag(Document document, Element parent, Nfce nfce) {
        Element pag = document.createElement("pag");
        parent.appendChild(pag);

        for (PagamentoNfce pagamento : nfce.getPagamentos()) {
            Element detPag = document.createElement("detPag");
            pag.appendChild(detPag);

            addElement(document, detPag, "tPag", pagamento.getMeioPagamento().getCodigo());
            addElement(document, detPag, "vPag", formatDecimal(pagamento.getValor(), 2));

            if (pagamento.getCnpjCredenciadora() != null) {
                Element card = document.createElement("card");
                detPag.appendChild(card);

                addElement(document, card, "CNPJ", pagamento.getCnpjCredenciadora());

                if (pagamento.getBandeiraOperadora() != null) {
                    addElement(document, card, "tBand", pagamento.getBandeiraOperadora());
                }

                if (pagamento.getNumeroAutorizacao() != null) {
                    addElement(document, card, "cAut", pagamento.getNumeroAutorizacao());
                }
            }
        }
    }

    private void criarElementoInfAdic(Document document, Element parent, Nfce nfce) {
        Element infAdic = document.createElement("infAdic");
        parent.appendChild(infAdic);

        StringBuilder infCpl = new StringBuilder();
        infCpl.append("Documento emitido por ME/EPP optante pelo Simples Nacional. ");
        infCpl.append("Não gera direito a crédito fiscal de IPI. ");
        infCpl.append("Não gera direito a crédito fiscal de ICMS.");

        addElement(document, infAdic, "infCpl", infCpl.toString());
    }

    private void addElement(Document document, Element parent, String tagName, String textContent) {
        Element element = document.createElement(tagName);
        if (textContent != null) {
            element.setTextContent(textContent);
        }
        parent.appendChild(element);
    }

    private String formatDecimal(BigDecimal value, int scale) {
        if (value == null) {
            return "0.00";
        }
        return value.setScale(scale, RoundingMode.HALF_UP).toString();
    }

    private String documentToString(Document document) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        
        return writer.toString();
    }
}
