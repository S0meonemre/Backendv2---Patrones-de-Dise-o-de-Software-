package com.financorp.serf.dto;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Nuevo lote de importación. El servidor fija el stock inicial y costo en EUR.")
public record ProductoRequest(
        @NotBlank @Size(max = 60) @Schema(example = "SMP-PE-001") String codigo,
        @NotBlank @Size(max = 200) @Schema(example = "Smartphone Nova X") String nombre,
        @NotBlank @Size(max = 2000) String descripcionTecnica,
        @NotBlank @Size(max = 100) @Schema(example = "Smartphone") String categoria,
        @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal costoImportacion,
        @NotBlank @Pattern(regexp = "CNY|PEN|USD|EUR") @Schema(example = "CNY") String monedaOrigen,
        @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal precioVentaSugerido,
        @NotBlank @Pattern(regexp = "CNY|PEN|USD|EUR") @Schema(example = "PEN") String monedaLocal,
        @NotBlank @Size(max = 200) String proveedor,
        @NotNull LocalDate fechaImportacion,
        @NotNull @PositiveOrZero Integer stock,
        @NotNull @Positive Long filialId) { }
