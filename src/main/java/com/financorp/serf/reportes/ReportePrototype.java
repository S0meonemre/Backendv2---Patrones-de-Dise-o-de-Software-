package com.financorp.serf.reportes;

/** Patron Prototype: cada plantilla de reporte sabe clonarse a si misma. */
public interface ReportePrototype {
    PlantillaReporte clonar();
}
