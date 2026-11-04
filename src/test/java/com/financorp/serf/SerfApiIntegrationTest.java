package com.financorp.serf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financorp.serf.repository.ProductoRepository;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"demo", "test"})
@WithMockUser(username = "qa", roles = "ADMIN")
@Transactional
class SerfApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired ProductoRepository productos;

    private Map<String, Object> venta(int cantidad) {
        var producto = productos.findAll().stream().filter(p -> p.getCodigo().equals("SP-PE-001")).findFirst().orElseThrow();
        return Map.of("numeroFactura", "TEST-001", "fechaVenta", LocalDate.now().toString(), "productoId", producto.getId(),
                "filialId", producto.getFilial().getId(), "cantidad", cantidad, "precioUnitario", 1400,
                "moneda", "PEN", "cliente", "Cliente de prueba", "vendedor", "Equipo QA", "metodoPago", "TRANSFERENCIA");
    }
    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder json(String path, Object body) throws Exception {
        return post(path).contentType("application/json").content(mapper.writeValueAsBytes(body));
    }

    @Test void registraVentaConvierteEurYDescuentaStock() throws Exception {
        var request = venta(100);
        var id = (Long) request.get("productoId");
        int anterior = productos.findById(id).orElseThrow().getStock();
        mvc.perform(json("/api/ventas", request)).andExpect(status().isCreated()).andExpect(jsonPath("$.montoEur").value(35000));
        assertEquals(anterior - 100, productos.findById(id).orElseThrow().getStock());
    }
    @Test void rechazaCantidadNegativaSinCambiarInventario() throws Exception {
        var request = venta(-10);
        var id = (Long) request.get("productoId");
        int anterior = productos.findById(id).orElseThrow().getStock();
        mvc.perform(json("/api/ventas", request)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.campos.cantidad").exists());
        assertEquals(anterior, productos.findById(id).orElseThrow().getStock());
    }
    @Test void rechazaStockInsuficienteYFacturaDuplicada() throws Exception {
        mvc.perform(json("/api/ventas", venta(100000))).andExpect(status().isConflict());
        mvc.perform(json("/api/ventas", venta(1))).andExpect(status().isCreated());
        mvc.perform(json("/api/ventas", venta(1))).andExpect(status().isConflict());
    }
    @Test void rechazaVentaEnOtraFilial() throws Exception {
        var request = new java.util.HashMap<>(venta(1));
        request.put("filialId", productos.findAll().stream().filter(p -> p.getCodigo().equals("LP-ES-001")).findFirst().orElseThrow().getFilial().getId());
        mvc.perform(json("/api/ventas", request)).andExpect(status().isBadRequest());
    }
    @Test void creaLoteConCostoCorporativoYStockInicial() throws Exception {
        var filialId = venta(1).get("filialId");
        Map<String, Object> request = new java.util.HashMap<>();
        request.putAll(Map.of("codigo", "TEST-LOTE", "nombre", "Smartphone QA", "descripcionTecnica", "Equipo de prueba",
                "categoria", "Smartphone", "costoImportacion", 100, "monedaOrigen", "CNY"));
        request.putAll(Map.of("precioVentaSugerido", 150, "monedaLocal", "PEN", "proveedor", "Shenzhen QA",
                "fechaImportacion", LocalDate.now().toString(), "stock", 150, "filialId", filialId));
        mvc.perform(json("/api/productos", request)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.costoImportacionEur").value(13)).andExpect(jsonPath("$.stockInicial").value(150));
        mvc.perform(json("/api/productos", request)).andExpect(status().isConflict());
    }
    @Test void reporteMensualFiltraVentasYConservaGastosDespuesDeVender() throws Exception {
        String periodo = LocalDate.now().toString().substring(0, 7);
        var request = Map.of("tipo", "MENSUAL", "periodo", periodo, "filialId", venta(1).get("filialId"));
        var before = mapper.readTree(mvc.perform(json("/api/reportes/generar", request)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertTrue(before.get("tablaVentas").size() > 0);
        for (var row : before.get("tablaVentas")) assertTrue(row.get("fecha").asText().startsWith(periodo));
        mvc.perform(json("/api/ventas", venta(1))).andExpect(status().isCreated());
        var after = mapper.readTree(mvc.perform(json("/api/reportes/generar", request)).andReturn().getResponse().getContentAsString());
        assertEquals(before.at("/resumen/gastosImportacion"), after.at("/resumen/gastosImportacion"));
        assertEquals(0, before.at("/resumen/ingresos").decimalValue().add(new java.math.BigDecimal("350.00")).compareTo(after.at("/resumen/ingresos").decimalValue()));
    }
    @Test void validaPeriodoFilialYDecoradoresOpcionales() throws Exception {
        mvc.perform(json("/api/reportes/generar", Map.of("tipo", "MENSUAL", "periodo", "2026-13"))).andExpect(status().isBadRequest());
        mvc.perform(json("/api/reportes/generar", Map.of("tipo", "ANUAL", "periodo", "2026", "filialId", 999999))).andExpect(status().isNotFound());
        mvc.perform(json("/api/reportes/generar", Map.of("tipo", "ANUAL", "periodo", "1900", "marcaAgua", false, "firmaDigital", false)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.resumen.ingresos").value(0)).andExpect(jsonPath("$.firmaDigital").doesNotExist());
    }
    @Test void exponeContratoCompletoYSwagger() throws Exception {
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/productos'].post").exists())
                .andExpect(jsonPath("$.paths['/api/productos/{id}'].get").exists())
                .andExpect(jsonPath("$.paths['/api/ventas'].post").exists())
                .andExpect(jsonPath("$.paths['/api/filiales'].get").exists())
                .andExpect(jsonPath("$.paths['/api/configuracion'].get").exists())
                .andExpect(jsonPath("$.paths['/api/reportes/generar'].post").exists());
        mvc.perform(get("/v3/api-docs.yaml")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("openapi:")));
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }
}
