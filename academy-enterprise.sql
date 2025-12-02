-- =============================================
-- 1. CONFIGURACIÓN INICIAL Y EXTENSIONES
-- =============================================
DROP SCHEMA IF EXISTS public CASCADE;
CREATE SCHEMA public;

-- Extensión para IDs seguros (se recomienda mencionar UUID para K8s)
CREATE EXTENSION IF NOT EXISTS "pgcrypto"; 

-- Función genérica para auditar la fecha de modificación
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_modificacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =============================================
-- 2. USUARIOS Y ROLES (SEGURIDAD Y AUDITORÍA CORE)
-- =============================================

-- TABLA BASE DE AUTENTICACIÓN
-- DECISIÓN: Usar BIGSERIAL para compatibilidad con Long en Java
-- RAZÓN: Evita desbordamiento (2B vs 9 quintillones) y consistencia con JPA
CREATE TABLE usuario (
    id_usuario BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMIN', 'PROFESOR', 'ALUMNO')),
    
    -- AUDITORÍA BASE (Sólo fecha_creacion, el resto de campos son sensibles)
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
);

-- TABLA DE DATOS PERSONALES (Relación 1:1, PII/GDPR)
CREATE TABLE datos_personales (
    id_usuario BIGINT PRIMARY KEY REFERENCES usuario(id_usuario),
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni_nie VARCHAR(20) UNIQUE,
    telefono VARCHAR(20),
    direccion TEXT,
    discapacidad_porcentaje DECIMAL(5,2) DEFAULT 0.00 CHECK (discapacidad_porcentaje >= 0.00), 
    es_familia_numerosa BOOLEAN DEFAULT FALSE,
    
    -- AUDITORÍA
    fecha_modificacion TIMESTAMP,
    updated_by BIGINT REFERENCES usuario(id_usuario)
);

-- TRIGGER para actualizar fecha_modificacion en datos_personales
CREATE TRIGGER trg_datos_personales_update
BEFORE UPDATE ON datos_personales
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

-- VISTAS de compatibilidad
CREATE VIEW alumno AS 
SELECT u.id_usuario as id_alumno, d.nombre, d.apellidos, u.email 
FROM usuario u JOIN datos_personales d ON u.id_usuario = d.id_usuario 
WHERE u.rol = 'ALUMNO';


-- =============================================
-- 3. TABLAS MAESTRAS Y ACADÉMICO (CON AUDITORÍA)
-- =============================================

-- TABLAS CON AUDITORÍA COMPLETA
CREATE TABLE comunidad (
    id_comunidad BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    capital VARCHAR(100),
    activo BOOLEAN DEFAULT TRUE,
    
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    created_by BIGINT REFERENCES usuario(id_usuario),
    updated_by BIGINT REFERENCES usuario(id_usuario)
);
CREATE TRIGGER trg_comunidad_update BEFORE UPDATE ON comunidad FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TABLE empresa (
    id_empresa BIGSERIAL PRIMARY KEY,
    cif VARCHAR(20) NOT NULL UNIQUE,
    nombre_legal VARCHAR(150) NOT NULL,
    direccion_fiscal TEXT,
    activo BOOLEAN DEFAULT TRUE,
    
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    created_by BIGINT REFERENCES usuario(id_usuario),
    updated_by BIGINT REFERENCES usuario(id_usuario)
);
CREATE TRIGGER trg_empresa_update BEFORE UPDATE ON empresa FOR EACH ROW EXECUTE FUNCTION update_timestamp();


CREATE TABLE centro (
    id_centro BIGSERIAL PRIMARY KEY,
    codigo_centro VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    id_empresa BIGINT REFERENCES empresa(id_empresa),
    id_comunidad BIGINT REFERENCES comunidad(id_comunidad),
    capacidad_maxima INT CHECK (capacidad_maxima >= 0),
    activo BOOLEAN DEFAULT TRUE,
    
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    created_by BIGINT REFERENCES usuario(id_usuario),
    updated_by BIGINT REFERENCES usuario(id_usuario)
);
CREATE TRIGGER trg_centro_update BEFORE UPDATE ON centro FOR EACH ROW EXECUTE FUNCTION update_timestamp();


CREATE TABLE materia (
    id_materia BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    created_by BIGINT REFERENCES usuario(id_usuario),
    updated_by BIGINT REFERENCES usuario(id_usuario)
);
CREATE TRIGGER trg_materia_update BEFORE UPDATE ON materia FOR EACH ROW EXECUTE FUNCTION update_timestamp();


CREATE TABLE formato (
    id_formato BIGSERIAL PRIMARY KEY, 
    nombre VARCHAR(50) NOT NULL,
    
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    created_by BIGINT REFERENCES usuario(id_usuario),
    updated_by BIGINT REFERENCES usuario(id_usuario)
);
CREATE TRIGGER trg_formato_update BEFORE UPDATE ON formato FOR EACH ROW EXECUTE FUNCTION update_timestamp();


CREATE TABLE curso (
    id_curso BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    id_materia BIGINT REFERENCES materia(id_materia),
    id_formato BIGINT REFERENCES formato(id_formato),
    precio_base DECIMAL(10, 2) NOT NULL CHECK (precio_base > 0), -- Constraint: precio > 0
    duracion_horas INT CHECK (duracion_horas > 0), -- Constraint: horas > 0
    activo BOOLEAN DEFAULT TRUE,
    
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    created_by BIGINT REFERENCES usuario(id_usuario),
    updated_by BIGINT REFERENCES usuario(id_usuario)
);
CREATE TRIGGER trg_curso_update BEFORE UPDATE ON curso FOR EACH ROW EXECUTE FUNCTION update_timestamp();


CREATE TABLE convocatoria (
    id_convocatoria BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE,
    id_curso BIGINT REFERENCES curso(id_curso),
    id_profesor BIGINT REFERENCES usuario(id_usuario),
    id_centro BIGINT REFERENCES centro(id_centro),
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL CHECK (fecha_fin > fecha_inicio), -- Constraint: fin posterior a inicio
    activo BOOLEAN DEFAULT TRUE,
    
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    created_by BIGINT REFERENCES usuario(id_usuario),
    updated_by BIGINT REFERENCES usuario(id_usuario)
);
CREATE TRIGGER trg_convocatoria_update BEFORE UPDATE ON convocatoria FOR EACH ROW EXECUTE FUNCTION update_timestamp();


-- =============================================
-- 4. FINANCIERO Y LÓGICA DE NEGOCIO (FINANCIAL CORE)
-- =============================================

CREATE TABLE entidad_subvencionadora (
    id_entidad BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150),
    codigo_oficial VARCHAR(50),
    
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    created_by BIGINT REFERENCES usuario(id_usuario),
    updated_by BIGINT REFERENCES usuario(id_usuario)
);
CREATE TRIGGER trg_entidad_update BEFORE UPDATE ON entidad_subvencionadora FOR EACH ROW EXECUTE FUNCTION update_timestamp();


CREATE TABLE matricula (
    id_matricula BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE,
    id_convocatoria BIGINT REFERENCES convocatoria(id_convocatoria),
    id_alumno BIGINT REFERENCES usuario(id_usuario),
    fecha_matricula TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- TRAZABILIDAD FINANCIERA
    precio_bruto DECIMAL(10, 2) NOT NULL CHECK (precio_bruto >= 0), 
    descuento_aplicado DECIMAL(10, 2) DEFAULT 0 CHECK (descuento_aplicado >= 0), 
    motivo_descuento VARCHAR(100),
    
    -- SUBVENCIONES
    id_entidad_subvencionadora BIGINT REFERENCES entidad_subvencionadora(id_entidad),
    importe_subvencionado DECIMAL(10,2) DEFAULT 0 CHECK (importe_subvencionado >= 0),
    
    -- Campo calculado (PostgreSQL 12+)
    precio_final DECIMAL(10, 2) GENERATED ALWAYS AS (precio_bruto - descuento_aplicado - importe_subvencionado) STORED, 
    
    estado_pago VARCHAR(20) DEFAULT 'PENDIENTE' CHECK (estado_pago IN ('PENDIENTE', 'PAGADO', 'CANCELADO')),
    
    -- AUDITORÍA
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    created_by BIGINT REFERENCES usuario(id_usuario),
    updated_by BIGINT REFERENCES usuario(id_usuario)
);
-- El trigger de precio se mantiene, pero ahora se combina con el created_by en el INSERT.
CREATE TRIGGER trg_calcular_precio_matricula
BEFORE INSERT ON matricula
FOR EACH ROW
EXECUTE FUNCTION aplicar_descuento_discapacidad(); 

CREATE TRIGGER trg_matricula_update BEFORE UPDATE ON matricula FOR EACH ROW EXECUTE FUNCTION update_timestamp();


-- Resto de tablas (Factura, Calificación) también llevan auditoría.
CREATE TABLE factura (
    id_factura BIGSERIAL PRIMARY KEY,
    numero_factura VARCHAR(50) UNIQUE NOT NULL,
    id_matricula BIGINT REFERENCES matricula(id_matricula),
    fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    base_imponible DECIMAL(10,2) NOT NULL CHECK (base_imponible >= 0),
    iva_porcentaje DECIMAL(5,2) DEFAULT 21.00 CHECK (iva_porcentaje >= 0),
    total DECIMAL(10,2) NOT NULL CHECK (total >= 0),
    datos_fiscales_cliente TEXT,
    
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    created_by BIGINT REFERENCES usuario(id_usuario),
    updated_by BIGINT REFERENCES usuario(id_usuario)
);
CREATE TRIGGER trg_factura_update BEFORE UPDATE ON factura FOR EACH ROW EXECUTE FUNCTION update_timestamp();


CREATE TABLE calificacion (
    id_calificacion BIGSERIAL PRIMARY KEY,
    id_matricula BIGINT REFERENCES matricula(id_matricula),
    nota DECIMAL(4, 2) CHECK (nota >= 0 AND nota <= 10), -- Constraint: nota entre 0 y 10
    comentarios TEXT,
    
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    created_by BIGINT REFERENCES usuario(id_usuario),
    updated_by BIGINT REFERENCES usuario(id_usuario)
);
CREATE TRIGGER trg_calificacion_update BEFORE UPDATE ON calificacion FOR EACH ROW EXECUTE FUNCTION update_timestamp();


-- =============================================
-- 5. LÓGICA DE NEGOCIO (DISCAPACIDAD TRIGGER)
-- =============================================
-- La función de descuento se mantiene igual, ya que solo interactúa con NEW.

CREATE OR REPLACE FUNCTION aplicar_descuento_discapacidad()
RETURNS TRIGGER AS $$
DECLARE
    v_porcentaje_discapacidad DECIMAL;
    v_precio_curso DECIMAL;
BEGIN
    -- 1. Obtener precio base del curso
    SELECT c.precio_base INTO v_precio_curso
    FROM convocatoria conv
    JOIN curso c ON conv.id_curso = c.id_curso
    WHERE conv.id_convocatoria = NEW.id_convocatoria;

    -- 2. Obtener discapacidad del alumno
    SELECT discapacidad_porcentaje INTO v_porcentaje_discapacidad
    FROM datos_personales
    WHERE id_usuario = NEW.id_alumno;

    -- 3. Lógica de negocio
    NEW.precio_bruto := v_precio_curso;
    
    IF v_porcentaje_discapacidad >= 33.0 THEN
        NEW.descuento_aplicado := v_precio_curso * 0.20; -- 20% descuento
        NEW.motivo_descuento := 'Descuento Discapacidad (+33%)';
    ELSE
        NEW.descuento_aplicado := 0;
        NEW.motivo_descuento := 'Sin descuento';
    END IF;

    -- Inicializa los campos de auditoría si se inserta desde un script o la app
    IF NEW.created_by IS NULL THEN
        NEW.created_by := (SELECT id_usuario FROM usuario WHERE rol = 'ADMIN' LIMIT 1); -- Asume un usuario Admin por defecto si no se especifica
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =============================================
-- 6. ÍNDICES (OPTIMIZACIÓN)
-- =============================================
-- (Los índices se mantienen igual, son cruciales para el rendimiento)
CREATE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_datos_apellidos ON datos_personales(apellidos);
CREATE INDEX idx_matricula_alumno ON matricula(id_alumno);
CREATE INDEX idx_matricula_convocatoria ON matricula(id_convocatoria);
CREATE INDEX idx_convocatoria_curso ON convocatoria(id_curso);