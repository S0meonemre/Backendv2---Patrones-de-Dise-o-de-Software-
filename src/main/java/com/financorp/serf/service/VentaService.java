package com.financorp.serf.service;

import com.financorp.serf.dto.VentaRequest;
import com.financorp.serf.controller.RecursoNoEncontradoException;
import com.financorp.serf.model.Filial;
import com.financorp.serf.model.Producto;
import com.financorp.serf.model.Venta;
import com.financorp.serf.repository.FilialRepository;
import com.financorp.serf.repository.ProductoRepository;
import com.financorp.serf.repository.VentaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final FilialRepository filialRepository;
    private final ConversionMonedaService conversionMonedaService;

    public VentaService(VentaRepository ventaRepository,
                         ProductoRepository productoRepository,
                         FilialRepository filialRepository,
                         ConversionMonedaService conversionMonedaService) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.filialRepository = filialRepository;
        this.conversionMonedaService = conversionMonedaService;
    }

    public List<Venta> listar() {
        return ventaRepository.findAll();
    }

    @Transactional
    public Venta registrar(VentaRequest request) {
        Producto producto = productoRepository.buscarParaVenta(request.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + request.getProductoId()));
        Filial filial = filialRepository.findById(request.getFilialId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Filial no encontrada: " + request.getFilialId()));

        if (producto.getFilial() == null || !producto.getFilial().getId().equals(filial.getId())) {
            throw new IllegalArgumentException("El producto no pertenece al inventario de esta filial.");
        }
        if (!filial.getMonedaLocal().equals(request.getMoneda())) {
            throw new IllegalArgumentException("La venta debe usar la moneda local de la filial.");
        }
        if (request.getFechaVenta().isBefore(producto.getFechaImportacion())) {
            throw new IllegalArgumentException("La venta no puede ser anterior a la importación.");
        }
        if (ventaRepository.existsByNumeroFacturaIgnoreCaseAndFilialId(request.getNumeroFactura().trim(), filial.getId())) {
            throw new IllegalStateException("La factura ya está registrada en esta filial.");
        }

        if (producto.getStock() < request.getCantidad()) {
            throw new IllegalStateException("Stock insuficiente para el producto " + producto.getCodigo());
        }
        producto.setStock(producto.getStock() - request.getCantidad());
        productoRepository.save(producto);

        Venta venta = new Venta();
        venta.setNumeroFactura(request.getNumeroFactura().trim().toUpperCase(java.util.Locale.ROOT));
        venta.setFechaVenta(request.getFechaVenta());
        venta.setProducto(producto);
        venta.setFilial(filial);
        venta.setCantidad(request.getCantidad());
        venta.setPrecioUnitario(request.getPrecioUnitario());
        venta.setMoneda(request.getMoneda());
        venta.setCliente(request.getCliente());
        venta.setVendedor(request.getVendedor());
        venta.setMetodoPago(request.getMetodoPago());

        var totalVenta = request.getPrecioUnitario().multiply(java.math.BigDecimal.valueOf(request.getCantidad()));
        venta.setMontoEur(conversionMonedaService.aMonedaCorporativa(totalVenta, request.getMoneda()));

        return ventaRepository.save(venta);
    }
}
