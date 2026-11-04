package com.financorp.serf.repository;

import com.financorp.serf.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    boolean existsByNumeroFacturaIgnoreCaseAndFilialId(String numeroFactura, Long filialId);
}
