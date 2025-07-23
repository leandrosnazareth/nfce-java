package com.leandrosnazareth.nfce_java.service;

import com.leandrosnazareth.nfce_java.config.NfceConfiguration;
import com.leandrosnazareth.nfce_java.repository.NfceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NumeracaoService {

    private final NfceRepository nfceRepository;
    private final NfceConfiguration nfceConfiguration;

    public Integer obterProximoNumero(Integer serie) {
        return nfceRepository.findMaxNumeroBySerieNfce(serie)
            .map(maxNumero -> maxNumero + 1)
            .orElse(nfceConfiguration.getNumeroInicial());
    }

    public boolean isNumeroDisponivel(Integer numero, Integer serie) {
        return !nfceRepository.existsByNumeroAndSerie(numero, serie);
    }
}
