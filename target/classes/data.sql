INSERT INTO filiales (nombre, pais, moneda_local)
SELECT 'FinanCorp Peru', 'Peru', 'PEN'
WHERE NOT EXISTS (SELECT 1 FROM filiales WHERE nombre = 'FinanCorp Peru');

INSERT INTO filiales (nombre, pais, moneda_local)
SELECT 'FinanCorp Espana', 'Espana', 'EUR'
WHERE NOT EXISTS (SELECT 1 FROM filiales WHERE nombre = 'FinanCorp Espana');

INSERT INTO filiales (nombre, pais, moneda_local)
SELECT 'FinanCorp Mexico', 'Mexico', 'USD'
WHERE NOT EXISTS (SELECT 1 FROM filiales WHERE nombre = 'FinanCorp Mexico');
