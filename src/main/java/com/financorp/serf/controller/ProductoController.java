package com.financorp.serf.controller;

import com.financorp.serf.model.Producto;
import com.financorp.serf.dto.ProductoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import com.financorp.serf.service.ProductoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Inventario de lotes importados por filial")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Listar productos, stock y filial")
    public List<Producto> listar() {
        return productoService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un producto por ID")
    public Producto obtener(@PathVariable Long id) {
        return productoService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar lote y convertir su costo unitario a EUR")
    public Producto crear(@Valid @RequestBody ProductoRequest producto) {
        return productoService.crear(producto);
    }
}
