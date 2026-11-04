package com.financorp.serf.service;

import com.financorp.serf.controller.RecursoNoEncontradoException;
import com.financorp.serf.dto.ProductoRequest;
import com.financorp.serf.model.Producto;
import com.financorp.serf.repository.ProductoRepository;
import com.financorp.serf.repository.FilialRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {
    private final ProductoRepository productos;
    private final FilialRepository filiales;
    private final ConversionMonedaService conversion;
    public ProductoService(ProductoRepository productos, FilialRepository filiales, ConversionMonedaService conversion) {
        this.productos = productos;
        this.filiales = filiales;
        this.conversion = conversion;
    }
    public List<Producto> listar() { return productos.findAll(); }
    public Producto obtener(Long id) {
        return productos.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + id));
    }
    @Transactional
    public Producto crear(ProductoRequest request) {
        String codigo = request.codigo().trim().toUpperCase(Locale.ROOT);
        if (productos.existsByCodigoIgnoreCase(codigo)) {
            throw new IllegalStateException("Ya existe ese código. Usa un código por lote y filial.");
        }
        var filial = filiales.findById(request.filialId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Filial no encontrada: " + request.filialId()));
        if (!filial.getMonedaLocal().equals(request.monedaLocal())) {
            throw new IllegalArgumentException("El precio sugerido debe usar la moneda local de la filial.");
        }
        Producto p = new Producto();
        p.setCodigo(codigo);
        p.setNombre(request.nombre().trim());
        p.setDescripcionTecnica(request.descripcionTecnica().trim());
        p.setCategoria(request.categoria().trim());
        p.setCostoImportacion(request.costoImportacion());
        p.setMonedaOrigen(request.monedaOrigen());
        p.setCostoImportacionEur(conversion.aMonedaCorporativa(request.costoImportacion(), request.monedaOrigen()));
        p.setPrecioVentaSugerido(request.precioVentaSugerido());
        p.setMonedaLocal(request.monedaLocal());
        p.setProveedor(request.proveedor().trim());
        p.setFechaImportacion(request.fechaImportacion());
        p.setStock(request.stock());
        p.setStockInicial(request.stock());
        p.setFilial(filial);
        return productos.save(p);
    }
}
