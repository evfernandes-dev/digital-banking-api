package com.digitalbanking.api.repository;

import com.digitalbanking.api.entity.Conta;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Repositório JPA da entidade Conta.
public interface ContaRepository extends JpaRepository<Conta, Long> {

    // Verifica se o número de conta já existe.
    boolean existsByNumero(String numero);

    // Busca a conta aplicando lock pessimista de escrita.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Conta c where c.id = :id")
    Optional<Conta> findByIdComBloqueio(@Param("id") Long id);

}
