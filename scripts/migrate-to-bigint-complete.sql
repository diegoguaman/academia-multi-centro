-- =============================================
-- Script Completo de Migración: SERIAL → BIGSERIAL
-- =============================================
-- Propósito: Migrar todas las tablas de SERIAL (INTEGER) a BIGSERIAL (BIGINT)
-- Compatibilidad: Consistente con Long en entidades Java
-- =============================================
-- IMPORTANTE: Ejecutar en orden, tabla por tabla
-- =============================================

BEGIN;

-- =============================================
-- 1. USUARIO (Tabla base - debe ir primero)
-- =============================================
DO $$
DECLARE
    current_val BIGINT;
BEGIN
    -- Obtener valor actual
    SELECT COALESCE(MAX(id_usuario), 0) INTO current_val FROM usuario;
    
    -- Cambiar tipo de columna
    ALTER TABLE usuario ALTER COLUMN id_usuario TYPE BIGINT;
    
    -- Recrear secuencia
    DROP SEQUENCE IF EXISTS usuario_id_usuario_seq CASCADE;
    CREATE SEQUENCE usuario_id_usuario_seq;
    PERFORM setval('usuario_id_usuario_seq', GREATEST(current_val, 1));
    ALTER TABLE usuario ALTER COLUMN id_usuario SET DEFAULT nextval('usuario_id_usuario_seq');
    ALTER SEQUENCE usuario_id_usuario_seq OWNED BY usuario.id_usuario;
    
    RAISE NOTICE '✓ Usuario migrado a BIGINT';
END $$;

-- Actualizar foreign keys que referencian usuario
ALTER TABLE datos_personales ALTER COLUMN id_usuario TYPE BIGINT;
ALTER TABLE datos_personales ALTER COLUMN updated_by TYPE BIGINT;

-- =============================================
-- 2. DATOS_PERSONALES (Depende de usuario)
-- =============================================
-- Ya actualizado arriba, solo verificar
RAISE NOTICE '✓ Datos personales actualizado';

-- =============================================
-- 3. COMUNIDAD
-- =============================================
DO $$
DECLARE
    current_val BIGINT;
BEGIN
    SELECT COALESCE(MAX(id_comunidad), 0) INTO current_val FROM comunidad;
    ALTER TABLE comunidad ALTER COLUMN id_comunidad TYPE BIGINT;
    DROP SEQUENCE IF EXISTS comunidad_id_comunidad_seq CASCADE;
    CREATE SEQUENCE comunidad_id_comunidad_seq;
    PERFORM setval('comunidad_id_comunidad_seq', GREATEST(current_val, 1));
    ALTER TABLE comunidad ALTER COLUMN id_comunidad SET DEFAULT nextval('comunidad_id_comunidad_seq');
    ALTER SEQUENCE comunidad_id_comunidad_seq OWNED BY comunidad.id_comunidad;
    ALTER TABLE comunidad ALTER COLUMN created_by TYPE BIGINT;
    ALTER TABLE comunidad ALTER COLUMN updated_by TYPE BIGINT;
    RAISE NOTICE '✓ Comunidad migrado a BIGINT';
END $$;

-- =============================================
-- 4. EMPRESA
-- =============================================
DO $$
DECLARE
    current_val BIGINT;
BEGIN
    SELECT COALESCE(MAX(id_empresa), 0) INTO current_val FROM empresa;
    ALTER TABLE empresa ALTER COLUMN id_empresa TYPE BIGINT;
    DROP SEQUENCE IF EXISTS empresa_id_empresa_seq CASCADE;
    CREATE SEQUENCE empresa_id_empresa_seq;
    PERFORM setval('empresa_id_empresa_seq', GREATEST(current_val, 1));
    ALTER TABLE empresa ALTER COLUMN id_empresa SET DEFAULT nextval('empresa_id_empresa_seq');
    ALTER SEQUENCE empresa_id_empresa_seq OWNED BY empresa.id_empresa;
    ALTER TABLE empresa ALTER COLUMN created_by TYPE BIGINT;
    ALTER TABLE empresa ALTER COLUMN updated_by TYPE BIGINT;
    RAISE NOTICE '✓ Empresa migrado a BIGINT';
END $$;

-- =============================================
-- 5. CENTRO (Depende de empresa y comunidad)
-- =============================================
DO $$
DECLARE
    current_val BIGINT;
BEGIN
    SELECT COALESCE(MAX(id_centro), 0) INTO current_val FROM centro;
    ALTER TABLE centro ALTER COLUMN id_centro TYPE BIGINT;
    DROP SEQUENCE IF EXISTS centro_id_centro_seq CASCADE;
    CREATE SEQUENCE centro_id_centro_seq;
    PERFORM setval('centro_id_centro_seq', GREATEST(current_val, 1));
    ALTER TABLE centro ALTER COLUMN id_centro SET DEFAULT nextval('centro_id_centro_seq');
    ALTER SEQUENCE centro_id_centro_seq OWNED BY centro.id_centro;
    ALTER TABLE centro ALTER COLUMN id_empresa TYPE BIGINT;
    ALTER TABLE centro ALTER COLUMN id_comunidad TYPE BIGINT;
    ALTER TABLE centro ALTER COLUMN created_by TYPE BIGINT;
    ALTER TABLE centro ALTER COLUMN updated_by TYPE BIGINT;
    RAISE NOTICE '✓ Centro migrado a BIGINT';
END $$;

-- =============================================
-- 6. MATERIA
-- =============================================
DO $$
DECLARE
    current_val BIGINT;
BEGIN
    SELECT COALESCE(MAX(id_materia), 0) INTO current_val FROM materia;
    ALTER TABLE materia ALTER COLUMN id_materia TYPE BIGINT;
    DROP SEQUENCE IF EXISTS materia_id_materia_seq CASCADE;
    CREATE SEQUENCE materia_id_materia_seq;
    PERFORM setval('materia_id_materia_seq', GREATEST(current_val, 1));
    ALTER TABLE materia ALTER COLUMN id_materia SET DEFAULT nextval('materia_id_materia_seq');
    ALTER SEQUENCE materia_id_materia_seq OWNED BY materia.id_materia;
    ALTER TABLE materia ALTER COLUMN created_by TYPE BIGINT;
    ALTER TABLE materia ALTER COLUMN updated_by TYPE BIGINT;
    RAISE NOTICE '✓ Materia migrado a BIGINT';
END $$;

-- =============================================
-- 7. FORMATO
-- =============================================
DO $$
DECLARE
    current_val BIGINT;
BEGIN
    SELECT COALESCE(MAX(id_formato), 0) INTO current_val FROM formato;
    ALTER TABLE formato ALTER COLUMN id_formato TYPE BIGINT;
    DROP SEQUENCE IF EXISTS formato_id_formato_seq CASCADE;
    CREATE SEQUENCE formato_id_formato_seq;
    PERFORM setval('formato_id_formato_seq', GREATEST(current_val, 1));
    ALTER TABLE formato ALTER COLUMN id_formato SET DEFAULT nextval('formato_id_formato_seq');
    ALTER SEQUENCE formato_id_formato_seq OWNED BY formato.id_formato;
    ALTER TABLE formato ALTER COLUMN created_by TYPE BIGINT;
    ALTER TABLE formato ALTER COLUMN updated_by TYPE BIGINT;
    RAISE NOTICE '✓ Formato migrado a BIGINT';
END $$;

-- =============================================
-- 8. CURSO (Depende de materia y formato)
-- =============================================
DO $$
DECLARE
    current_val BIGINT;
BEGIN
    SELECT COALESCE(MAX(id_curso), 0) INTO current_val FROM curso;
    ALTER TABLE curso ALTER COLUMN id_curso TYPE BIGINT;
    DROP SEQUENCE IF EXISTS curso_id_curso_seq CASCADE;
    CREATE SEQUENCE curso_id_curso_seq;
    PERFORM setval('curso_id_curso_seq', GREATEST(current_val, 1));
    ALTER TABLE curso ALTER COLUMN id_curso SET DEFAULT nextval('curso_id_curso_seq');
    ALTER SEQUENCE curso_id_curso_seq OWNED BY curso.id_curso;
    ALTER TABLE curso ALTER COLUMN id_materia TYPE BIGINT;
    ALTER TABLE curso ALTER COLUMN id_formato TYPE BIGINT;
    ALTER TABLE curso ALTER COLUMN created_by TYPE BIGINT;
    ALTER TABLE curso ALTER COLUMN updated_by TYPE BIGINT;
    RAISE NOTICE '✓ Curso migrado a BIGINT';
END $$;

-- =============================================
-- 9. CONVOCATORIA (Depende de curso, profesor, centro)
-- =============================================
DO $$
DECLARE
    current_val BIGINT;
BEGIN
    SELECT COALESCE(MAX(id_convocatoria), 0) INTO current_val FROM convocatoria;
    ALTER TABLE convocatoria ALTER COLUMN id_convocatoria TYPE BIGINT;
    DROP SEQUENCE IF EXISTS convocatoria_id_convocatoria_seq CASCADE;
    CREATE SEQUENCE convocatoria_id_convocatoria_seq;
    PERFORM setval('convocatoria_id_convocatoria_seq', GREATEST(current_val, 1));
    ALTER TABLE convocatoria ALTER COLUMN id_convocatoria SET DEFAULT nextval('convocatoria_id_convocatoria_seq');
    ALTER SEQUENCE convocatoria_id_convocatoria_seq OWNED BY convocatoria.id_convocatoria;
    ALTER TABLE convocatoria ALTER COLUMN id_curso TYPE BIGINT;
    ALTER TABLE convocatoria ALTER COLUMN id_profesor TYPE BIGINT;
    ALTER TABLE convocatoria ALTER COLUMN id_centro TYPE BIGINT;
    ALTER TABLE convocatoria ALTER COLUMN created_by TYPE BIGINT;
    ALTER TABLE convocatoria ALTER COLUMN updated_by TYPE BIGINT;
    RAISE NOTICE '✓ Convocatoria migrado a BIGINT';
END $$;

-- =============================================
-- 10. ENTIDAD_SUBVENCIONADORA
-- =============================================
DO $$
DECLARE
    current_val BIGINT;
BEGIN
    SELECT COALESCE(MAX(id_entidad), 0) INTO current_val FROM entidad_subvencionadora;
    ALTER TABLE entidad_subvencionadora ALTER COLUMN id_entidad TYPE BIGINT;
    DROP SEQUENCE IF EXISTS entidad_subvencionadora_id_entidad_seq CASCADE;
    CREATE SEQUENCE entidad_subvencionadora_id_entidad_seq;
    PERFORM setval('entidad_subvencionadora_id_entidad_seq', GREATEST(current_val, 1));
    ALTER TABLE entidad_subvencionadora ALTER COLUMN id_entidad SET DEFAULT nextval('entidad_subvencionadora_id_entidad_seq');
    ALTER SEQUENCE entidad_subvencionadora_id_entidad_seq OWNED BY entidad_subvencionadora.id_entidad;
    ALTER TABLE entidad_subvencionadora ALTER COLUMN created_by TYPE BIGINT;
    ALTER TABLE entidad_subvencionadora ALTER COLUMN updated_by TYPE BIGINT;
    RAISE NOTICE '✓ Entidad subvencionadora migrado a BIGINT';
END $$;

-- =============================================
-- 11. MATRICULA (Depende de convocatoria, alumno, entidad)
-- =============================================
DO $$
DECLARE
    current_val BIGINT;
BEGIN
    SELECT COALESCE(MAX(id_matricula), 0) INTO current_val FROM matricula;
    ALTER TABLE matricula ALTER COLUMN id_matricula TYPE BIGINT;
    DROP SEQUENCE IF EXISTS matricula_id_matricula_seq CASCADE;
    CREATE SEQUENCE matricula_id_matricula_seq;
    PERFORM setval('matricula_id_matricula_seq', GREATEST(current_val, 1));
    ALTER TABLE matricula ALTER COLUMN id_matricula SET DEFAULT nextval('matricula_id_matricula_seq');
    ALTER SEQUENCE matricula_id_matricula_seq OWNED BY matricula.id_matricula;
    ALTER TABLE matricula ALTER COLUMN id_convocatoria TYPE BIGINT;
    ALTER TABLE matricula ALTER COLUMN id_alumno TYPE BIGINT;
    ALTER TABLE matricula ALTER COLUMN id_entidad_subvencionadora TYPE BIGINT;
    ALTER TABLE matricula ALTER COLUMN created_by TYPE BIGINT;
    ALTER TABLE matricula ALTER COLUMN updated_by TYPE BIGINT;
    RAISE NOTICE '✓ Matricula migrado a BIGINT';
END $$;

-- =============================================
-- 12. FACTURA (Depende de matricula)
-- =============================================
DO $$
DECLARE
    current_val BIGINT;
BEGIN
    SELECT COALESCE(MAX(id_factura), 0) INTO current_val FROM factura;
    ALTER TABLE factura ALTER COLUMN id_factura TYPE BIGINT;
    DROP SEQUENCE IF EXISTS factura_id_factura_seq CASCADE;
    CREATE SEQUENCE factura_id_factura_seq;
    PERFORM setval('factura_id_factura_seq', GREATEST(current_val, 1));
    ALTER TABLE factura ALTER COLUMN id_factura SET DEFAULT nextval('factura_id_factura_seq');
    ALTER SEQUENCE factura_id_factura_seq OWNED BY factura.id_factura;
    ALTER TABLE factura ALTER COLUMN id_matricula TYPE BIGINT;
    ALTER TABLE factura ALTER COLUMN created_by TYPE BIGINT;
    ALTER TABLE factura ALTER COLUMN updated_by TYPE BIGINT;
    RAISE NOTICE '✓ Factura migrado a BIGINT';
END $$;

-- =============================================
-- 13. CALIFICACION (Depende de matricula)
-- =============================================
DO $$
DECLARE
    current_val BIGINT;
BEGIN
    SELECT COALESCE(MAX(id_calificacion), 0) INTO current_val FROM calificacion;
    ALTER TABLE calificacion ALTER COLUMN id_calificacion TYPE BIGINT;
    DROP SEQUENCE IF EXISTS calificacion_id_calificacion_seq CASCADE;
    CREATE SEQUENCE calificacion_id_calificacion_seq;
    PERFORM setval('calificacion_id_calificacion_seq', GREATEST(current_val, 1));
    ALTER TABLE calificacion ALTER COLUMN id_calificacion SET DEFAULT nextval('calificacion_id_calificacion_seq');
    ALTER SEQUENCE calificacion_id_calificacion_seq OWNED BY calificacion.id_calificacion;
    ALTER TABLE calificacion ALTER COLUMN id_matricula TYPE BIGINT;
    ALTER TABLE calificacion ALTER COLUMN created_by TYPE BIGINT;
    ALTER TABLE calificacion ALTER COLUMN updated_by TYPE BIGINT;
    RAISE NOTICE '✓ Calificacion migrado a BIGINT';
END $$;

-- =============================================
-- VERIFICACIÓN FINAL
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
          AND c.data_type = 'integer'
        ORDER BY t.table_name, c.column_name
    LOOP
        RAISE NOTICE '❌ ERROR: %%.% tiene tipo INTEGER (debería ser BIGINT)', table_name, column_name;
        error_count := error_count + 1;
    END LOOP;
    
    IF error_count = 0 THEN
        RAISE NOTICE '✅ TODAS las columnas ID tienen tipo BIGINT correctamente';
    ELSE
        RAISE WARNING '⚠️ Se encontraron % columnas con tipo incorrecto', error_count;
    END IF;
    
    RAISE NOTICE '========================================';
END $$;

-- =============================================
-- VERIFICACIÓN DE SECUENCIAS
-- =============================================
DO $$
DECLARE
    seq_name TEXT;
    seq_type TEXT;
    error_count INT := 0;
BEGIN
    RAISE NOTICE '========================================';
    RAISE NOTICE 'VERIFICACIÓN DE SECUENCIAS';
    RAISE NOTICE '========================================';
    
    FOR seq_name, seq_type IN
        SELECT 
            sequence_name,
            data_type
        FROM information_schema.sequences
        WHERE sequence_schema = 'public'
          AND data_type = 'integer'
        ORDER BY sequence_name
    LOOP
        RAISE NOTICE '❌ ERROR: Secuencia % tiene tipo INTEGER (debería ser BIGINT)', seq_name;
        error_count := error_count + 1;
    END LOOP;
    
    IF error_count = 0 THEN
        RAISE NOTICE '✅ TODAS las secuencias tienen tipo BIGINT correctamente';
    ELSE
        RAISE WARNING '⚠️ Se encontraron % secuencias con tipo incorrecto', error_count;
    END IF;
    
    RAISE NOTICE '========================================';
END $$;

COMMIT;

-- =============================================
-- RESUMEN FINAL
-- =============================================
SELECT 
    'Migración completada' as status,
    COUNT(*) FILTER (WHERE data_type = 'bigint') as columnas_bigint,
    COUNT(*) FILTER (WHERE data_type = 'integer') as columnas_integer
FROM information_schema.columns
WHERE table_schema = 'public'
  AND column_name LIKE 'id_%';

