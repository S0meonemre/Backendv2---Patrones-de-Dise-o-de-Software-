package com.financorp.serf.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ventas", uniqueConstraints = @UniqueConstraint(columnNames = {"filial_id", "numero_factura"}))
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_factura")
    private String numeroFactura;
    private LocalDate fechaVenta;

    @ManyToOne
    private Producto producto;

    @ManyToOne
    private Filial filial;

    private Integer cantidad;
    private BigDecimal precioUnitario;
    private String moneda;
    private BigDecimal montoEur;

    private String cliente;
    private String vendedor;
    private String metodoPago;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }

    public LocalDate getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDate fechaVenta) { this.fechaVenta = fechaVenta; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Filial getFilial() { return filial; }
    public void setFilial(Filial filial) { this.filial = filial; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public BigDecimal getMontoEur() { return montoEur; }
    public void setMontoEur(BigDecimal montoEur) { this.montoEur = montoEur; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getVendedor() { return vendedor; }
    public void setVendedor(String vendedor) { this.vendedor = vendedor; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
}
