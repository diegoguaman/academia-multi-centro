-- =============================================
-- 1. LIMPIEZA Y REINICIO TOTAL DE SECUENCIAS
-- Comando "destructivo" necesario en DEV para un inicio limpio.
-- =============================================
TRUNCATE TABLE factura, calificacion, matricula, convocatoria, curso, datos_personales, usuario, centro, empresa, comunidad, materia, formato, entidad_subvencionadora CASCADE;

-- Reinicio explícito de todas las secuencias SERIAL que generan IDs.
SELECT setval(pg_get_serial_sequence('usuario', 'id_usuario'), 1, false);
SELECT setval(pg_get_serial_sequence('empresa', 'id_empresa'), 1, false); 
SELECT setval(pg_get_serial_sequence('comunidad', 'id_comunidad'), 1, false); 
SELECT setval(pg_get_serial_sequence('materia', 'id_materia'), 1, false); 
SELECT setval(pg_get_serial_sequence('formato', 'id_formato'), 1, false); 
SELECT setval(pg_get_serial_sequence('curso', 'id_curso'), 1, false); -- AGREGADO
SELECT setval(pg_get_serial_sequence('convocatoria', 'id_convocatoria'), 1, false); -- AGREGADO


-- =============================================
-- 2. PADRES INICIALES Y USUARIOS (Nivel 0)
-- =============================================

-- A. Insertar Usuarios (Necesario para created_by y profesor/alumno)
INSERT INTO usuario (email, password_hash, rol) VALUES 
('admin@innoqa.com', 'hash_admin_secreto', 'ADMIN'); -- ID 1
INSERT INTO usuario (email, password_hash, rol) VALUES 
('profe.java@demo.com', 'hash_profe_123', 'PROFESOR'), -- ID 2
('alumno.disca@demo.com', 'hash_alumno_123', 'ALUMNO'), -- ID 3
('alumno.normal@demo.com', 'hash_alumno_456', 'ALUMNO'); -- ID 4

-- B. Insertar Empresa y Comunidad (Padres del Centro)
INSERT INTO empresa (cif, nombre_legal, direccion_fiscal, created_by) VALUES 
('B12345678', 'InnoTraining S.L.', 'C/ Innovación 1', 1); -- ID 1 para EMPRESA

INSERT INTO comunidad (codigo, nombre, capital, created_by) VALUES 
('ES-MAD', 'Comunidad de Madrid', 'Madrid', 1),
('ES-CAT', 'Catalunya', 'Barcelona', 1);

-- C. Insertar Catálogos de Curso (Padres directos del Curso)
INSERT INTO materia (nombre, created_by) VALUES 
('Desarrollo', 1), -- ID 1 para Materia
('Data', 1);        -- ID 2 para Materia

INSERT INTO formato (nombre, created_by) VALUES 
('Online', 1),      -- ID 1 para Formato
('Presencial', 1);  -- ID 2 para Formato

INSERT INTO entidad_subvencionadora (nombre, codigo_oficial, created_by) VALUES
('Fondo Social Europeo', 'FSE-2025', 1);

-- =============================================
-- 3. POBLADO DE CENTROS (Nivel 1)
-- =============================================
INSERT INTO centro (codigo_centro, nombre, id_empresa, id_comunidad, capacidad_maxima, created_by) VALUES 
('CEN-MAD-01', 'Campus Sur', 1, 1, 100, 1);


-- =============================================
-- 4. POBLADO DE ACADÉMICO Y CORE (Nivel 2)
-- ¡La inserción de CURSO DEBE ser exitosa aquí!
-- =============================================

-- A. Insertar Cursos (Hijo de Materia ID 1 y Formato ID 1)
INSERT INTO curso (nombre, id_materia, id_formato, precio_base, duracion_horas, created_by) VALUES 
('Master Java Spring Boot & Cloud', 1, 1, 1000.00, 300, 1), -- ID 1 para Curso
('SQL Avanzado para Analistas', 2, 1, 500.00, 50, 1);      -- ID 2 para Curso

-- B. Insertar Convocatoria (Referencia a Curso ID 1 y Profesor ID 2)
-- Esto ya no debería fallar porque la secuencia de curso está reiniciada y el insert es inmediato.
INSERT INTO convocatoria (codigo, id_curso, id_profesor, id_centro, fecha_inicio, fecha_fin, created_by) VALUES 
('CONV-2025-JAVA-01', 1, 2, 5, '2025-02-01', '2025-06-30', 1); 


-- =============================================
-- 5. POBLADO DE DATOS PERSONALES Y MATRICULACIÓN (Nivel 3)
-- =============================================

-- Datos Personales (Referencia a Usuarios 2, 3, 4)
INSERT INTO datos_personales (id_usuario, nombre, apellidos, dni_nie, discapacidad_porcentaje) VALUES 
(2, 'James', 'Gosling', '11111111H', 0.00),
(3, 'Diego', 'Estudiante', '22222222X', 33.00), 
(4, 'Maria', 'García', '33333333Y', 0.00);

-- Matriculación (Prueba del Trigger de Descuento y Auditoría)
INSERT INTO matricula (codigo, id_convocatoria, id_alumno, created_by) VALUES 
('MAT-001', 1, 3, 3), -- Diego (Discapacidad)
('MAT-002', 1, 4, 4); -- Maria (Normal)

-- 6. SIMULACIÓN DE TRAZABILIDAD Y FACTURACIÓN
UPDATE datos_personales SET nombre = 'Mariana', updated_by = 1 WHERE id_usuario = 4;

INSERT INTO factura (numero_factura, id_matricula, base_imponible, total, created_by) VALUES
('2025/INV-001', 2, 1000.00, 1210.00, 1); 

-- =============================================
-- 7. VERIFICACIÓN FINAL
-- =============================================

SELECT 
    m.codigo, 
    d.nombre, 
    m.precio_bruto, 
    m.descuento_aplicado, 
    m.precio_final, 
    m.motivo_descuento
FROM matricula m
JOIN datos_personales d ON m.id_alumno = d.id_usuario;