package com.financorp.serf.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String codigo;
    private String nombre;
    @Column(length = 2000)
    private String descripcionTecnica;
    private String categoria;

    private BigDecimal costoImportacion;
    private String monedaOrigen;

    private BigDecimal precioVentaSugerido;
    private String monedaLocal;

    private String proveedor;
    private LocalDate fechaImportacion;
    private Integer stock;
    private Integer stockInicial;
    private BigDecimal costoImportacionEur;
    @ManyToOne
    private Filial filial;

    public Integer getStockInicial() { return stockInicial; }
    public void setStockInicial(Integer stockInicial) { this.stockInicial = stockInicial; }
    public BigDecimal getCostoImportacionEur() { return costoImportacionEur; }
    public void setCostoImportacionEur(BigDecimal costoImportacionEur) { this.costoImportacionEur = costoImportacionEur; }
    public Filial getFilial() { return filial; }
    public void setFilial(Filial filial) { this.filial = filial; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcionTecnica() { return descripcionTecnica; }
    public void setDescripcionTecnica(String descripcionTecnica) { this.descripcionTecnica = descripcionTecnica; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public BigDecimal getCostoImportacion() { return costoImportacion; }
    public void setCostoImportacion(BigDecimal costoImportacion) { this.costoImportacion = costoImportacion; }

    public String getMonedaOrigen() { return monedaOrigen; }
    public void setMonedaOrigen(String monedaOrigen) { this.monedaOrigen = monedaOrigen; }

    public BigDecimal getPrecioVentaSugerido() { return precioVentaSugerido; }
    public void setPrecioVentaSugerido(BigDecimal precioVentaSugerido) { this.precioVentaSugerido = precioVentaSugerido; }

    public String getMonedaLocal() { return monedaLocal; }
    public void setMonedaLocal(String monedaLocal) { this.monedaLocal = monedaLocal; }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    public LocalDate getFechaImportacion() { return fechaImportacion; }
    public void setFechaImportacion(LocalDate fechaImportacion) { this.fechaImportacion = fechaImportacion; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
