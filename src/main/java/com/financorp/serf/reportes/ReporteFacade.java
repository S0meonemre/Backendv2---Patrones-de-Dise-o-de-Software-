package com.financorp.serf.reportes;

import com.financorp.serf.config.ConfiguracionGlobal;
import com.financorp.serf.controller.RecursoNoEncontradoException;
import com.financorp.serf.dto.ReporteRequest;
import com.financorp.serf.model.Producto;
import com.financorp.serf.model.Venta;
import com.financorp.serf.repository.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Facade orquesta los patrones y presenta un solo punto de entrada al controller. */
@Service
public class ReporteFacade {
    private final ProductoRepository productos;
    private final VentaRepository ventas;
    private final FilialRepository filiales;
    private final CatalogoPlantillas plantillas;
    private final FirmaReporteService firma;
    public ReporteFacade(ProductoRepository productos, VentaRepository ventas, FilialRepository filiales,
                         CatalogoPlantillas plantillas, FirmaReporteService firma) {
        this.productos = productos; this.ventas = ventas; this.filiales = filiales;
        this.plantillas = plantillas; this.firma = firma;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> generarReporte(ReporteRequest request) {
        PeriodoReporte rango = PeriodoReporte.de(request.getTipo(), request.getPeriodo());
        Long filialId = request.getFilialId();
        String filial = filialId == null ? "Todas las filiales" : filiales.findById(filialId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Filial no encontrada: " + filialId)).getNombre();
        List<Venta> seleccion = ventas.findAll().stream()
                .filter(v -> rango.contiene(v.getFechaVenta()))
                .filter(v -> filialId == null || filialId.equals(v.getFilial().getId()))
                .sorted(Comparator.comparing(Venta::getFechaVenta).thenComparing(Venta::getId)).toList();
        List<Producto> importaciones = productos.findAll().stream()
                .filter(p -> rango.contiene(p.getFechaImportacion()))
                .filter(p -> filialId == null || (p.getFilial() != null && filialId.equals(p.getFilial().getId()))).toList();
        BigDecimal ingresos = seleccion.stream().map(Venta::getMontoEur).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal gastos = importaciones.stream().map(this::costoLote).reduce(BigDecimal.ZERO, BigDecimal::add);
        PlantillaReporte plantilla = plantillas.clonar(request.getTipo());
        if (seleccion.isEmpty()) plantilla.getConclusionesBase().add("No se registraron ventas en el período y alcance seleccionados.");
        plantilla.getConclusionesBase().add("El saldo compara ingresos con importaciones del período; no representa utilidad contable neta.");
        if (importaciones.stream().anyMatch(p -> p.getStockInicial() == null)) {
            plantilla.getConclusionesBase().add("Existen lotes históricos sin stock inicial: sus costos son estimados a partir del stock disponible.");
        }
        var paises = agrupar(seleccion, v -> v.getFilial().getPais());
        List<SeccionReporte> secciones = List.of(
                new SeccionCompuesta("Ingresos por país", paises),
                new SeccionCompuesta("Ingresos por categoría", agrupar(seleccion, v -> v.getProducto().getCategoria())),
                new SeccionCompuesta("Gastos de importación", importaciones.stream()
                        .map(p -> (SeccionReporte) new SeccionSimple(p.getNombre(), costoLote(p))).toList()));
        List<Map<String, Object>> tabla = seleccion.stream().map(v -> {
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("factura", v.getNumeroFactura()); fila.put("fecha", v.getFechaVenta().toString());
            fila.put("producto", v.getProducto().getNombre()); fila.put("filial", v.getFilial().getNombre());
            fila.put("cantidad", v.getCantidad()); fila.put("moneda", v.getMoneda());
            fila.put("totalLocal", v.getPrecioUnitario().multiply(BigDecimal.valueOf(v.getCantidad())));
            fila.put("totalEur", v.getMontoEur()); return fila;
        }).toList();
        List<Map<String, Object>> graficos = paises.stream().map(s -> {
            Map<String, Object> punto = new LinkedHashMap<>();
            punto.put("etiqueta", s.getNombre()); punto.put("valor", s.getTotal()); return punto;
        }).toList();
        ReporteComponent reporte = new ReporteBuilder().conPlantilla(plantilla).conPeriodo(request.getPeriodo())
                .conMonedaCorporativa(ConfiguracionGlobal.getInstance().getMonedaCorporativa()).conSecciones(secciones)
                .conResumen(Map.of("ingresos", ingresos, "gastosImportacion", gastos, "saldo", ingresos.subtract(gastos),
                        "operaciones", seleccion.size(), "unidadesVendidas", seleccion.stream().mapToInt(Venta::getCantidad).sum()))
                .conTablaVentas(tabla).conGraficos(graficos)
                .conAlcance(Map.of("filial", filial, "desde", rango.inicio().toString(), "hasta", rango.fin().toString())).build();
        if (request.isMarcaAgua()) reporte = new MarcaAguaDecorator(reporte);
        if (request.isFirmaDigital()) reporte = new FirmaDigitalDecorator(reporte, firma);
        return reporte.generarContenido();
    }

    private List<SeccionReporte> agrupar(List<Venta> datos, java.util.function.Function<Venta, String> clasificador) {
        Map<String, BigDecimal> totales = datos.stream().collect(Collectors.groupingBy(clasificador, TreeMap::new,
                Collectors.reducing(BigDecimal.ZERO, Venta::getMontoEur, BigDecimal::add)));
        return totales.entrySet().stream().map(e -> (SeccionReporte) new SeccionSimple(e.getKey(), e.getValue())).toList();
    }
    private BigDecimal costoLote(Producto p) {
        BigDecimal costo = p.getCostoImportacionEur() != null ? p.getCostoImportacionEur()
                : ConfiguracionGlobal.getInstance().convertirAMonedaCorporativa(p.getCostoImportacion(), p.getMonedaOrigen());
        int inicial = p.getStockInicial() != null ? p.getStockInicial() : p.getStock();
        return costo.multiply(BigDecimal.valueOf(inicial));
    }
}
