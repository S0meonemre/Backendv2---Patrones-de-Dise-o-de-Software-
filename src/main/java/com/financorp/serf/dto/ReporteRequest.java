package com.financorp.serf.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public class ReporteRequest {

    @NotBlank
    @Pattern(regexp = "MENSUAL|TRIMESTRAL|ANUAL")
    private String tipo;

    @NotBlank
    private String periodo;

    @Positive private Long filialId;
    private boolean marcaAgua = true;
    private boolean firmaDigital = true;

    public boolean isMarcaAgua() { return marcaAgua; }
    public void setMarcaAgua(boolean marcaAgua) { this.marcaAgua = marcaAgua; }
    public boolean isFirmaDigital() { return firmaDigital; }
    public void setFirmaDigital(boolean firmaDigital) { this.firmaDigital = firmaDigital; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public Long getFilialId() { return filialId; }
    public void setFilialId(Long filialId) { this.filialId = filialId; }
}
