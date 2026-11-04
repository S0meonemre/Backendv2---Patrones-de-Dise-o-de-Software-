package com.financorp.serf.controller;

import com.financorp.serf.dto.VentaRequest;
import com.financorp.serf.model.Venta;
import com.financorp.serf.service.VentaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/ventas")
@Tag(name = "Ventas", description = "Transacciones por filial con conversión a EUR y descuento de inventario")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    @Operation(summary = "Listar ventas con producto y filial")
    public List<Venta> listar() {
        return ventaService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar venta y descontar stock en una transacción")
    public Venta registrar(@Valid @RequestBody VentaRequest request) {
        return ventaService.registrar(request);
    }
}
