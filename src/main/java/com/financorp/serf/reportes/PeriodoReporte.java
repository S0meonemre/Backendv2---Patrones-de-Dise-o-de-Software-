package com.financorp.serf.reportes;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.DateTimeException;

/** Valida el período antes de consultar datos. El extremo final es inclusivo. */
public record PeriodoReporte(LocalDate inicio, LocalDate fin) {
    public static PeriodoReporte de(String tipo, String periodo) {
        try {
            if (periodo == null || tipo == null) throw new IllegalArgumentException();
            return switch (tipo) {
                case "MENSUAL" -> {
                    if (!periodo.matches("[1-9][0-9]{3}-[0-9]{2}")) throw new IllegalArgumentException();
                    YearMonth mes = YearMonth.parse(periodo);
                    yield new PeriodoReporte(mes.atDay(1), mes.atEndOfMonth());
                }
                case "TRIMESTRAL" -> {
                    if (!periodo.matches("[1-9][0-9]{3}-Q[1-4]")) throw new IllegalArgumentException();
                    int anio = Integer.parseInt(periodo.substring(0, 4));
                    int trimestre = Integer.parseInt(periodo.substring(6));
                    LocalDate inicio = LocalDate.of(anio, (trimestre - 1) * 3 + 1, 1);
                    yield new PeriodoReporte(inicio, inicio.plusMonths(3).minusDays(1));
                }
                case "ANUAL" -> {
                    if (!periodo.matches("[1-9][0-9]{3}")) throw new IllegalArgumentException();
                    int anio = Integer.parseInt(periodo);
                    yield new PeriodoReporte(LocalDate.of(anio, 1, 1), LocalDate.of(anio, 12, 31));
                }
                default -> throw new IllegalArgumentException();
            };
        } catch (IllegalArgumentException | DateTimeException ex) {
            throw new IllegalArgumentException("Tipo o período inválido. Usa MENSUAL: AAAA-MM, TRIMESTRAL: AAAA-Q1 a Q4, ANUAL: AAAA.");
        }
    }
    public boolean contiene(LocalDate fecha) {
        return fecha != null && !fecha.isBefore(inicio) && !fecha.isAfter(fin);
    }
}
