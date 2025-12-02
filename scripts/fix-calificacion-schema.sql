-- =============================================
-- Script de Verificación y Población de Datos
-- =============================================
-- Propósito: Verificar schema y poblar datos de prueba
-- Uso: Ejecutar después de crear la DB con academy-enterprise.sql
-- =============================================

-- =============================================
-- 1. VERIFICACIÓN DE SCHEMA
-- =============================================
DO $$
DECLARE
    table_name TEXT;
    column_name TEXT;
    data_type TEXT;
    error_count INT := 0;
BEGIN
    RAISE NOTICE '========================================';
    RAISE NOTICE 'VERIFICACIÓN DE TIPOS DE DATOS';
    RAISE NOTICE '========================================';
    
    FOR table_name, column_name, data_type IN
        SELECT 
            t.table_name,
            c.column_name,
            c.data_type
        FROM information_schema.tables t
        JOIN information_schema.columns c ON t.table_name = c.table_name
        WHERE t.table_schema = 'public'
          AND c.column_name LIKE 'id_%'
          AND c.data_type NOT IN ('bigint', 'bigserial')
        ORDER BY t.table_name, c.column_name
    LOOP
        RAISE NOTICE '❌ ERROR: %%.% tiene tipo % (debería ser BIGINT)', 
            table_name, column_name, data_type;
        error_count := error_count + 1;
    END LOOP;
    
    IF error_count = 0 THEN
        RAISE NOTICE '✅ TODAS las columnas ID tienen tipo BIGINT/BIGSERIAL correctamente';
    ELSE
        RAISE WARNING '⚠️ Se encontraron % columnas con tipo incorrecto', error_count;
    END IF;
    
    RAISE NOTICE '========================================';
END $$;

-- =============================================
-- 2. VERIFICACIÓN DE TABLAS EXISTENTES
-- =============================================
SELECT 
    'Verificación de tablas' as check_type,
    COUNT(*) as total_tables
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_type = 'BASE TABLE';

-- =============================================
-- 3. POBLACIÓN DE DATOS DE PRUEBA
-- =============================================

-- 3.1. Crear usuario ADMIN
INSERT INTO usuario (email, password_hash, rol, activo)
VALUES 
    ('admin@academia.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', true)
ON CONFLICT (email) DO NOTHING
RETURNING id_usuario;

-- 3.2. Crear datos personales para admin
INSERT INTO datos_personales (id_usuario, nombre, apellidos)
SELECT 
    u.id_usuario,
    'Admin',
    'Sistema'
FROM usuario u
WHERE u.email = 'admin@academia.com'
ON CONFLICT (id_usuario) DO NOTHING;

-- 3.3. Crear comunidad de prueba
INSERT INTO comunidad (codigo, nombre, capital, activo, created_by)
SELECT 
    'MAD',
    'Comunidad de Madrid',
    'Madrid',
    true,
    u.id_usuario
FROM usuario u
WHERE u.email = 'admin@academia.com'
ON CONFLICT (codigo) DO NOTHING
RETURNING id_comunidad;

-- 3.4. Crear empresa de prueba
INSERT INTO empresa (cif, nombre_legal, direccion_fiscal, activo, created_by)
SELECT 
    'B12345678',
    'Academia Multi-Centro SL',
    'Calle Ejemplo 123, Madrid',
    true,
    u.id_usuario
FROM usuario u
WHERE u.email = 'admin@academia.com'
ON CONFLICT (cif) DO NOTHING
RETURNING id_empresa;

-- 3.5. Crear centro de prueba
INSERT INTO centro (codigo_centro, nombre, id_empresa, id_comunidad, capacidad_maxima, activo, created_by)
SELECT 
    'CENTRO-001',
    'Centro Principal Madrid',
    e.id_empresa,
    c.id_comunidad,
    100,
    true,
    u.id_usuario
FROM usuario u
CROSS JOIN empresa e
CROSS JOIN comunidad c
WHERE u.email = 'admin@academia.com'
  AND e.cif = 'B12345678'
  AND c.codigo = 'MAD'
ON CONFLICT (codigo_centro) DO NOTHING
RETURNING id_centro;

-- 3.6. Crear materia de prueba
INSERT INTO materia (nombre, activo, created_by)
SELECT 
    'Java Avanzado',
    true,
    u.id_usuario
FROM usuario u
WHERE u.email = 'admin@academia.com'
ON CONFLICT DO NOTHING
RETURNING id_materia;

-- 3.7. Crear formato de prueba
INSERT INTO formato (nombre, created_by)
SELECT 
    'Presencial',
    u.id_usuario
FROM usuario u
WHERE u.email = 'admin@academia.com'
ON CONFLICT DO NOTHING
RETURNING id_formato;

-- 3.8. Crear curso de prueba
INSERT INTO curso (nombre, id_materia, id_formato, precio_base, duracion_horas, activo, created_by)
SELECT 
    'Spring Boot Enterprise',
    m.id_materia,
    f.id_formato,
    500.00,
    40,
    true,
    u.id_usuario
FROM usuario u
CROSS JOIN materia m
CROSS JOIN formato f
WHERE u.email = 'admin@academia.com'
  AND m.nombre = 'Java Avanzado'
  AND f.nombre = 'Presencial'
ON CONFLICT DO NOTHING
RETURNING id_curso;

-- =============================================
-- 4. VERIFICACIÓN DE DATOS INSERTADOS
-- =============================================
SELECT 
    'Datos insertados' as check_type,
    (SELECT COUNT(*) FROM usuario) as usuarios,
    (SELECT COUNT(*) FROM datos_personales) as datos_personales,
    (SELECT COUNT(*) FROM comunidad) as comunidades,
    (SELECT COUNT(*) FROM empresa) as empresas,
    (SELECT COUNT(*) FROM centro) as centros,
    (SELECT COUNT(*) FROM materia) as materias,
    (SELECT COUNT(*) FROM formato) as formatos,
    (SELECT COUNT(*) FROM curso) as cursos;

-- =============================================
-- 5. VERIFICACIÓN DE INTEGRIDAD REFERENCIAL
-- =============================================
DO $$
DECLARE
    fk_violations INT;
BEGIN
    RAISE NOTICE '========================================';
    RAISE NOTICE 'VERIFICACIÓN DE INTEGRIDAD REFERENCIAL';
    RAISE NOTICE '========================================';
    
    -- Verificar foreign keys rotas
    SELECT COUNT(*) INTO fk_violations
    FROM matricula m
    LEFT JOIN convocatoria c ON m.id_convocatoria = c.id_convocatoria
    LEFT JOIN usuario u ON m.id_alumno = u.id_usuario
    WHERE m.id_convocatoria IS NOT NULL AND c.id_convocatoria IS NULL
       OR m.id_alumno IS NOT NULL AND u.id_usuario IS NULL;
    
    IF fk_violations = 0 THEN
        RAISE NOTICE '✅ Todas las foreign keys son válidas';
    ELSE
        RAISE WARNING '⚠️ Se encontraron % violaciones de foreign keys', fk_violations;
    END IF;
    
    RAISE NOTICE '========================================';
END $$;

-- =============================================
-- 6. RESUMEN FINAL
-- =============================================
SELECT 
    '✅ Verificación completada' as status,
    NOW() as fecha_verificacion;

