package com.financorp.serf.repository;

import com.financorp.serf.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsByCodigoIgnoreCase(String codigo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.id = :id")
    Optional<Producto> buscarParaVenta(@Param("id") Long id);
}
