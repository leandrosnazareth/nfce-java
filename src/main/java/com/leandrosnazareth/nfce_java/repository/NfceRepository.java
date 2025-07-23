package com.leandrosnazareth.nfce_java.repository;
import com.leandrosnazareth.nfce_java.model.entity.Nfce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NfceRepository extends JpaRepository<Nfce, Long> {

    Optional<Nfce> findByChaveAcesso(String chaveAcesso);

    List<Nfce> findByStatus(Nfce.StatusNfce status);

    List<Nfce> findByEmitenteRazaoSocialContainingIgnoreCase(String razaoSocial);

    @Query("SELECT n FROM Nfce n WHERE n.dataEmissao BETWEEN :dataInicio AND :dataFim")
    List<Nfce> findByDataEmissaoBetween(@Param("dataInicio") LocalDateTime dataInicio, 
                                        @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT MAX(n.numero) FROM Nfce n WHERE n.serie = :serie")
    Optional<Integer> findMaxNumeroBySerieNfce(@Param("serie") Integer serie);

    @Query("SELECT n FROM Nfce n WHERE n.emitenteCnpj = :cnpj ORDER BY n.dataEmissao DESC")
    List<Nfce> findByEmitenteCnpjOrderByDataEmissaoDesc(@Param("cnpj") String cnpj);

    @Query("SELECT COUNT(n) FROM Nfce n WHERE n.status = :status")
    Long countByStatus(@Param("status") Nfce.StatusNfce status);

    @Query("SELECT n FROM Nfce n WHERE n.destinatarioCpfCnpj = :cpfCnpj ORDER BY n.dataEmissao DESC")
    List<Nfce> findByDestinatarioCpfCnpjOrderByDataEmissaoDesc(@Param("cpfCnpj") String cpfCnpj);

    boolean existsByNumeroAndSerie(Integer numero, Integer serie);
}
