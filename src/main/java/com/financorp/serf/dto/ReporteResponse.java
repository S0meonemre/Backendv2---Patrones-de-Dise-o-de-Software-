package com.financorp.serf.dto;

import java.math.BigDecimal;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Reporte ensamblado, con secciones jerárquicas, tabla, gráficos y decoradores opcionales.")
public record ReporteResponse(String tipo, String periodo, String monedaCorporativa, String titulo,
        List<Seccion> secciones, List<String> conclusiones, Resumen resumen,
        List<FilaVenta> tablaVentas, List<DatoGrafico> graficos, Alcance alcance,
        String marcaAgua, Firma firmaDigital) {
    public record Seccion(String nombre, BigDecimal total, List<Seccion> subsecciones) { }
    public record Resumen(BigDecimal ingresos, BigDecimal gastosImportacion, BigDecimal saldo,
                          int operaciones, int unidadesVendidas) { }
    public record FilaVenta(String factura, String fecha, String producto, String filial,
                           int cantidad, String moneda, BigDecimal totalLocal, BigDecimal totalEur) { }
    public record DatoGrafico(String etiqueta, BigDecimal valor) { }
    public record Alcance(String filial, String desde, String hasta) { }
    public record Firma(String algoritmo, String valor, String clavePublica, String contenidoBase64, String alcance) { }
}
