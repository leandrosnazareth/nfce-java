package com.leandrosnazareth.nfce_java.service;

import com.leandrosnazareth.nfce_java.config.NfceConfiguration;
import com.leandrosnazareth.nfce_java.model.dto.NfceRequestDto;
import com.leandrosnazareth.nfce_java.model.dto.NfceResponseDto;
import com.leandrosnazareth.nfce_java.model.entity.Nfce;
import com.leandrosnazareth.nfce_java.model.entity.ItemNfce;
import com.leandrosnazareth.nfce_java.model.entity.PagamentoNfce;
import com.leandrosnazareth.nfce_java.repository.NfceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NfceService {

    private final NfceRepository nfceRepository;
    private final NfceConfiguration nfceConfiguration;
    private final ChaveAcessoService chaveAcessoService;
    private final QrCodeService qrCodeService;
    private final XmlService xmlService;
    private final NumeracaoService numeracaoService;

    public NfceResponseDto criarNfce(NfceRequestDto request) {
        log.info("Criando nova NFC-e - Série: {}, Número: {}", request.getSerie(), request.getNumero());

        // Validar se já existe NFC-e com mesmo número e série
        if (nfceRepository.existsByNumeroAndSerie(request.getNumero(), request.getSerie())) {
            throw new IllegalArgumentException("Já existe uma NFC-e com o número " + request.getNumero() + " e série " + request.getSerie());
        }

        // Criar entidade NFC-e
        Nfce nfce = new Nfce();
        mapearRequestParaEntity(request, nfce);

        // Calcular totais
        calcularTotais(nfce);

        // Gerar chave de acesso
        String chaveAcesso = chaveAcessoService.gerarChaveAcessoNfce(
            nfceConfiguration.getUf(),
            nfce.getDataEmissao(),
            nfce.getEmitenteCnpj(),
            nfce.getSerie(),
            nfce.getNumero(),
            1 // Tipo emissão normal
        );
        nfce.setChaveAcesso(chaveAcesso);

        // Gerar URL do QR Code
        String urlQrCode = qrCodeService.gerarUrlQrCode(
            chaveAcesso,
            nfce.getAmbiente().getCodigo(),
            nfce.getDataEmissao(),
            nfce.getValorTotalNota(),
            nfce.getDestinatarioCpfCnpj()
        );
        nfce.setUrlConsulta(urlQrCode);
        nfce.setQrCode(urlQrCode);

        // Gerar XML
        String xmlAssinado = xmlService.gerarXmlNfce(nfce);
        nfce.setXmlAssinado(xmlAssinado);

        // Definir status inicial
        nfce.setStatus(Nfce.StatusNfce.RASCUNHO);

        // Salvar no banco
        nfce = nfceRepository.save(nfce);

        log.info("NFC-e criada com sucesso - ID: {}, Chave: {}", nfce.getId(), chaveAcesso);

        return mapearEntityParaResponse(nfce);
    }

    @Transactional(readOnly = true)
    public NfceResponseDto buscarPorId(Long id) {
        Nfce nfce = nfceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("NFC-e não encontrada com ID: " + id));
        
        return mapearEntityParaResponse(nfce);
    }

    @Transactional(readOnly = true)
    public NfceResponseDto buscarPorChaveAcesso(String chaveAcesso) {
        Nfce nfce = nfceRepository.findByChaveAcesso(chaveAcesso)
            .orElseThrow(() -> new IllegalArgumentException("NFC-e não encontrada com chave de acesso: " + chaveAcesso));
        
        return mapearEntityParaResponse(nfce);
    }

    @Transactional(readOnly = true)
    public List<NfceResponseDto> buscarPorStatus(String status) {
        Nfce.StatusNfce statusEnum = Nfce.StatusNfce.valueOf(status.toUpperCase());
        List<Nfce> nfces = nfceRepository.findByStatus(statusEnum);
        
        return nfces.stream()
            .map(this::mapearEntityParaResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NfceResponseDto> buscarPorEmitente(String cnpj) {
        List<Nfce> nfces = nfceRepository.findByEmitenteCnpjOrderByDataEmissaoDesc(cnpj);
        
        return nfces.stream()
            .map(this::mapearEntityParaResponse)
            .collect(Collectors.toList());
    }

    public NfceResponseDto assinarNfce(Long id) {
        Nfce nfce = nfceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("NFC-e não encontrada com ID: " + id));

        if (nfce.getStatus() != Nfce.StatusNfce.RASCUNHO) {
            throw new IllegalStateException("NFC-e deve estar em status RASCUNHO para ser assinada");
        }

        // Aqui seria implementada a assinatura digital
        // Por simplicidade, apenas alteramos o status
        nfce.setStatus(Nfce.StatusNfce.ASSINADA);
        nfce = nfceRepository.save(nfce);

        log.info("NFC-e assinada - ID: {}", id);

        return mapearEntityParaResponse(nfce);
    }

    public NfceResponseDto enviarNfce(Long id) {
        Nfce nfce = nfceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("NFC-e não encontrada com ID: " + id));

        if (nfce.getStatus() != Nfce.StatusNfce.ASSINADA) {
            throw new IllegalStateException("NFC-e deve estar assinada para ser enviada");
        }

        // Aqui seria implementado o envio para a SEFAZ
        // Por simplicidade, apenas alteramos o status
        nfce.setStatus(Nfce.StatusNfce.ENVIADA);
        nfce = nfceRepository.save(nfce);

        log.info("NFC-e enviada - ID: {}", id);

        return mapearEntityParaResponse(nfce);
    }

    public NfceResponseDto autorizarNfce(Long id) {
        Nfce nfce = nfceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("NFC-e não encontrada com ID: " + id));

        if (nfce.getStatus() != Nfce.StatusNfce.ENVIADA) {
            throw new IllegalStateException("NFC-e deve estar enviada para ser autorizada");
        }

        // Simular autorização
        nfce.setStatus(Nfce.StatusNfce.AUTORIZADA);
        nfce.setProtocoloAutorizacao("135" + System.currentTimeMillis());
        nfce.setDataAutorizacao(LocalDateTime.now());
        nfce = nfceRepository.save(nfce);

        log.info("NFC-e autorizada - ID: {}, Protocolo: {}", id, nfce.getProtocoloAutorizacao());

        return mapearEntityParaResponse(nfce);
    }

    public NfceResponseDto cancelarNfce(Long id, String justificativa) {
        Nfce nfce = nfceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("NFC-e não encontrada com ID: " + id));

        if (nfce.getStatus() != Nfce.StatusNfce.AUTORIZADA) {
            throw new IllegalStateException("Apenas NFC-e autorizadas podem ser canceladas");
        }

        if (justificativa == null || justificativa.length() < 15) {
            throw new IllegalArgumentException("Justificativa deve ter pelo menos 15 caracteres");
        }

        // Aqui seria implementado o cancelamento na SEFAZ
        nfce.setStatus(Nfce.StatusNfce.CANCELADA);
        nfce = nfceRepository.save(nfce);

        log.info("NFC-e cancelada - ID: {}, Justificativa: {}", id, justificativa);

        return mapearEntityParaResponse(nfce);
    }

    @Transactional(readOnly = true)
    public Integer obterProximoNumero(Integer serie) {
        return numeracaoService.obterProximoNumero(serie);
    }

    private void mapearRequestParaEntity(NfceRequestDto request, Nfce nfce) {
        // Dados básicos
        nfce.setNumero(request.getNumero());
        nfce.setSerie(request.getSerie());
        nfce.setNaturezaOperacao(request.getNaturezaOperacao());
        nfce.setDataEmissao(LocalDateTime.now());
        nfce.setTipoOperacao(Nfce.TipoOperacao.SAIDA);
        nfce.setFinalidadeEmissao(Nfce.FinalidadeEmissao.NORMAL);
        nfce.setFormaPagamento(Nfce.FormaPagamento.PAGAMENTO_A_VISTA);
        
        // Ambiente (vem da configuração)
        String ambiente = nfceConfiguration.getAmbiente();
        nfce.setAmbiente("producao".equals(ambiente) ? Nfce.Ambiente.PRODUCAO : Nfce.Ambiente.HOMOLOGACAO);

        // Emitente
        NfceRequestDto.EmitenteDto emitenteDto = request.getEmitente();
        nfce.setEmitenteCnpj(emitenteDto.getCnpj());
        nfce.setEmitenteRazaoSocial(emitenteDto.getRazaoSocial());
        nfce.setEmitenteNomeFantasia(emitenteDto.getNomeFantasia());
        nfce.setEmitenteLogradouro(emitenteDto.getLogradouro());
        nfce.setEmitenteNumero(emitenteDto.getNumero());
        nfce.setEmitenteBairro(emitenteDto.getBairro());
        nfce.setEmitenteMunicipio(emitenteDto.getMunicipio());
        nfce.setEmitenteUf(emitenteDto.getUf());
        nfce.setEmitenteCep(emitenteDto.getCep());
        nfce.setEmitenteCodigoMunicipio(emitenteDto.getCodigoMunicipio());
        nfce.setEmitenteInscricaoEstadual(emitenteDto.getInscricaoEstadual());
        
        // Converter enum do DTO para enum da entidade
        nfce.setEmitenteRegimeTributario(
            Nfce.RegimeTributario.valueOf(emitenteDto.getRegimeTributario().name())
        );

        // Destinatário (opcional)
        if (request.getDestinatario() != null) {
            nfce.setDestinatarioCpfCnpj(request.getDestinatario().getCpfCnpj());
            nfce.setDestinatarioNome(request.getDestinatario().getNome());
        }

        // Itens
        for (NfceRequestDto.ItemDto itemDto : request.getItens()) {
            ItemNfce item = new ItemNfce();
            item.setNfce(nfce);
            item.setNumeroItem(itemDto.getNumeroItem());
            item.setCodigoProduto(itemDto.getCodigoProduto());
            item.setDescricao(itemDto.getDescricao());
            item.setNcm(itemDto.getNcm());
            item.setCfop(itemDto.getCfop());
            item.setUnidadeComercial(itemDto.getUnidadeComercial());
            item.setQuantidadeComercial(itemDto.getQuantidadeComercial());
            item.setValorUnitarioComercial(itemDto.getValorUnitarioComercial());
            
            // Valor total bruto = quantidade * valor unitário
            BigDecimal valorTotalBruto = itemDto.getQuantidadeComercial()
                .multiply(itemDto.getValorUnitarioComercial());
            item.setValorTotalBruto(valorTotalBruto);
            
            // Unidade tributável = unidade comercial (por padrão)
            item.setUnidadeTributavel(itemDto.getUnidadeComercial());
            item.setQuantidadeTributavel(itemDto.getQuantidadeComercial());
            item.setValorUnitarioTributavel(itemDto.getValorUnitarioComercial());
            
            item.setValorDesconto(itemDto.getValorDesconto());
            item.setIncluiNoTotal(ItemNfce.IncluiNoTotal.SIM);
            
            // Converter enums
            item.setOrigemMercadoria(
                ItemNfce.OrigemMercadoria.valueOf(itemDto.getOrigemMercadoria().name())
            );
            
            item.setCstIcms(itemDto.getCstIcms());
            item.setModalidadeBcIcms(itemDto.getModalidadeBcIcms());
            item.setBaseCalculoIcms(itemDto.getBaseCalculoIcms());
            item.setAliquotaIcms(itemDto.getAliquotaIcms());
            
            // Calcular valor ICMS
            if (itemDto.getBaseCalculoIcms() != null && itemDto.getAliquotaIcms() != null) {
                BigDecimal valorIcms = itemDto.getBaseCalculoIcms()
                    .multiply(itemDto.getAliquotaIcms())
                    .divide(BigDecimal.valueOf(100));
                item.setValorIcms(valorIcms);
            }
            
            nfce.getItens().add(item);
        }

        // Pagamentos
        for (NfceRequestDto.PagamentoDto pagamentoDto : request.getPagamentos()) {
            PagamentoNfce pagamento = new PagamentoNfce();
            pagamento.setNfce(nfce);
            pagamento.setMeioPagamento(
                PagamentoNfce.MeioPagamento.valueOf(pagamentoDto.getMeioPagamento().name())
            );
            pagamento.setValor(pagamentoDto.getValor());
            pagamento.setCnpjCredenciadora(pagamentoDto.getCnpjCredenciadora());
            pagamento.setBandeiraOperadora(pagamentoDto.getBandeiraOperadora());
            pagamento.setNumeroAutorizacao(pagamentoDto.getNumeroAutorizacao());
            
            nfce.getPagamentos().add(pagamento);
        }
    }

    private void calcularTotais(Nfce nfce) {
        BigDecimal valorTotalProdutos = BigDecimal.ZERO;
        BigDecimal valorDesconto = BigDecimal.ZERO;
        BigDecimal baseCalculoIcms = BigDecimal.ZERO;
        BigDecimal valorIcms = BigDecimal.ZERO;

        // Calcular totais dos itens
        for (ItemNfce item : nfce.getItens()) {
            valorTotalProdutos = valorTotalProdutos.add(item.getValorTotalBruto());
            
            if (item.getValorDesconto() != null) {
                valorDesconto = valorDesconto.add(item.getValorDesconto());
            }
            
            if (item.getBaseCalculoIcms() != null) {
                baseCalculoIcms = baseCalculoIcms.add(item.getBaseCalculoIcms());
            }
            
            if (item.getValorIcms() != null) {
                valorIcms = valorIcms.add(item.getValorIcms());
            }
        }

        nfce.setValorTotalProdutos(valorTotalProdutos);
        nfce.setValorDesconto(valorDesconto);
        nfce.setBaseCalculoIcms(baseCalculoIcms);
        nfce.setValorIcms(valorIcms);
        
        // Valor total da nota = valor produtos - desconto + outras despesas
        BigDecimal valorTotalNota = valorTotalProdutos.subtract(valorDesconto);
        
        if (nfce.getValorFrete() != null) {
            valorTotalNota = valorTotalNota.add(nfce.getValorFrete());
        }
        
        if (nfce.getValorSeguro() != null) {
            valorTotalNota = valorTotalNota.add(nfce.getValorSeguro());
        }
        
        if (nfce.getOutrasDespesas() != null) {
            valorTotalNota = valorTotalNota.add(nfce.getOutrasDespesas());
        }
        
        nfce.setValorTotalNota(valorTotalNota);
        
        // Inicializar campos opcionais com zero se nulos
        if (nfce.getValorFrete() == null) nfce.setValorFrete(BigDecimal.ZERO);
        if (nfce.getValorSeguro() == null) nfce.setValorSeguro(BigDecimal.ZERO);
        if (nfce.getOutrasDespesas() == null) nfce.setOutrasDespesas(BigDecimal.ZERO);
        if (nfce.getBaseCalculoIcmsSt() == null) nfce.setBaseCalculoIcmsSt(BigDecimal.ZERO);
        if (nfce.getValorIcmsSt() == null) nfce.setValorIcmsSt(BigDecimal.ZERO);
        if (nfce.getValorPis() == null) nfce.setValorPis(BigDecimal.ZERO);
        if (nfce.getValorCofins() == null) nfce.setValorCofins(BigDecimal.ZERO);
    }

    private NfceResponseDto mapearEntityParaResponse(Nfce nfce) {
        NfceResponseDto response = new NfceResponseDto();
        
        response.setId(nfce.getId());
        response.setNumero(nfce.getNumero());
        response.setSerie(nfce.getSerie());
        response.setChaveAcesso(nfce.getChaveAcesso());
        response.setDataEmissao(nfce.getDataEmissao());
        response.setNaturezaOperacao(nfce.getNaturezaOperacao());
        response.setStatus(nfce.getStatus().name());
        response.setProtocoloAutorizacao(nfce.getProtocoloAutorizacao());
        response.setDataAutorizacao(nfce.getDataAutorizacao());
        response.setQrCode(nfce.getQrCode());
        response.setUrlConsulta(nfce.getUrlConsulta());
        response.setCreatedAt(nfce.getCreatedAt());
        response.setUpdatedAt(nfce.getUpdatedAt());

        // Emitente
        NfceResponseDto.EmitenteResponseDto emitenteResponse = new NfceResponseDto.EmitenteResponseDto();
        emitenteResponse.setCnpj(nfce.getEmitenteCnpj());
        emitenteResponse.setRazaoSocial(nfce.getEmitenteRazaoSocial());
        emitenteResponse.setNomeFantasia(nfce.getEmitenteNomeFantasia());
        emitenteResponse.setLogradouro(nfce.getEmitenteLogradouro());
        emitenteResponse.setNumero(nfce.getEmitenteNumero());
        emitenteResponse.setBairro(nfce.getEmitenteBairro());
        emitenteResponse.setMunicipio(nfce.getEmitenteMunicipio());
        emitenteResponse.setUf(nfce.getEmitenteUf());
        emitenteResponse.setCep(nfce.getEmitenteCep());
        emitenteResponse.setCodigoMunicipio(nfce.getEmitenteCodigoMunicipio());
        emitenteResponse.setInscricaoEstadual(nfce.getEmitenteInscricaoEstadual());
        emitenteResponse.setRegimeTributario(nfce.getEmitenteRegimeTributario().name());
        response.setEmitente(emitenteResponse);

        // Destinatário
        if (nfce.getDestinatarioCpfCnpj() != null) {
            NfceResponseDto.DestinatarioResponseDto destinatarioResponse = new NfceResponseDto.DestinatarioResponseDto();
            destinatarioResponse.setCpfCnpj(nfce.getDestinatarioCpfCnpj());
            destinatarioResponse.setNome(nfce.getDestinatarioNome());
            response.setDestinatario(destinatarioResponse);
        }

        // Itens
        List<NfceResponseDto.ItemResponseDto> itensResponse = nfce.getItens().stream()
            .map(item -> {
                NfceResponseDto.ItemResponseDto itemResponse = new NfceResponseDto.ItemResponseDto();
                itemResponse.setNumeroItem(item.getNumeroItem());
                itemResponse.setCodigoProduto(item.getCodigoProduto());
                itemResponse.setDescricao(item.getDescricao());
                itemResponse.setNcm(item.getNcm());
                itemResponse.setCfop(item.getCfop());
                itemResponse.setUnidadeComercial(item.getUnidadeComercial());
                itemResponse.setQuantidadeComercial(item.getQuantidadeComercial());
                itemResponse.setValorUnitarioComercial(item.getValorUnitarioComercial());
                itemResponse.setValorTotalBruto(item.getValorTotalBruto());
                itemResponse.setValorDesconto(item.getValorDesconto());
                itemResponse.setOrigemMercadoria(item.getOrigemMercadoria().name());
                itemResponse.setCstIcms(item.getCstIcms());
                itemResponse.setBaseCalculoIcms(item.getBaseCalculoIcms());
                itemResponse.setAliquotaIcms(item.getAliquotaIcms());
                itemResponse.setValorIcms(item.getValorIcms());
                return itemResponse;
            })
            .collect(Collectors.toList());
        response.setItens(itensResponse);

        // Pagamentos
        List<NfceResponseDto.PagamentoResponseDto> pagamentosResponse = nfce.getPagamentos().stream()
            .map(pagamento -> {
                NfceResponseDto.PagamentoResponseDto pagamentoResponse = new NfceResponseDto.PagamentoResponseDto();
                pagamentoResponse.setMeioPagamento(pagamento.getMeioPagamento().name());
                pagamentoResponse.setValor(pagamento.getValor());
                pagamentoResponse.setCnpjCredenciadora(pagamento.getCnpjCredenciadora());
                pagamentoResponse.setBandeiraOperadora(pagamento.getBandeiraOperadora());
                pagamentoResponse.setNumeroAutorizacao(pagamento.getNumeroAutorizacao());
                return pagamentoResponse;
            })
            .collect(Collectors.toList());
        response.setPagamentos(pagamentosResponse);

        // Totais
        NfceResponseDto.TotaisResponseDto totaisResponse = new NfceResponseDto.TotaisResponseDto();
        totaisResponse.setValorTotalProdutos(nfce.getValorTotalProdutos());
        totaisResponse.setValorTotalNota(nfce.getValorTotalNota());
        totaisResponse.setValorDesconto(nfce.getValorDesconto());
        totaisResponse.setBaseCalculoIcms(nfce.getBaseCalculoIcms());
        totaisResponse.setValorIcms(nfce.getValorIcms());
        totaisResponse.setValorPis(nfce.getValorPis());
        totaisResponse.setValorCofins(nfce.getValorCofins());
        response.setTotais(totaisResponse);

        return response;
    }
}
