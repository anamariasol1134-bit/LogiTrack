
DROP DATABASE IF EXISTS logitrack;
CREATE DATABASE logitrack CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE logitrack;

SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE municipio (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(80) NOT NULL,
    departamento VARCHAR(80) NOT NULL,
    CONSTRAINT uq_municipio UNIQUE (nombre, departamento)
) ENGINE=InnoDB;


CREATE TABLE usuario (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre           VARCHAR(100) NOT NULL,
    username         VARCHAR(50)  NOT NULL UNIQUE,
    password         VARCHAR(100) NOT NULL,
    rol              ENUM('ADMIN','EMPLEADO') NOT NULL DEFAULT 'EMPLEADO',
    estado_cuenta    ENUM('PENDIENTE','ACTIVA','RECHAZADA','INACTIVA') NOT NULL DEFAULT 'PENDIENTE',
    fecha_creacion   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    aprobado_por_id  BIGINT   NULL,
    fecha_aprobacion DATETIME NULL,
    motivo_rechazo   VARCHAR(255) NULL,
    CONSTRAINT fk_usuario_aprobador FOREIGN KEY (aprobado_por_id) REFERENCES usuario(id)
) ENGINE=InnoDB;

CREATE INDEX idx_usuario_estado ON usuario(estado_cuenta);


CREATE TABLE proveedor (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL,
    nit          VARCHAR(20)  NOT NULL UNIQUE,
    telefono     VARCHAR(20),
    correo       VARCHAR(100),
    municipio_id BIGINT NOT NULL,
    activo       BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_proveedor_municipio FOREIGN KEY (municipio_id) REFERENCES municipio(id)
) ENGINE=InnoDB;


CREATE TABLE bodega (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL UNIQUE,
    ubicacion    VARCHAR(150) NOT NULL,
    municipio_id BIGINT       NOT NULL,
    capacidad    INT          NOT NULL,
    encargado    VARCHAR(100) NOT NULL,
    CONSTRAINT ck_bodega_capacidad CHECK (capacidad > 0),
    CONSTRAINT fk_bodega_municipio FOREIGN KEY (municipio_id) REFERENCES municipio(id)
) ENGINE=InnoDB;


CREATE TABLE producto (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku          VARCHAR(30)   NOT NULL UNIQUE,
    nombre       VARCHAR(100)  NOT NULL,
    categoria    VARCHAR(60)   NOT NULL,
    precio       DECIMAL(12,2) NOT NULL,
    stock_minimo INT           NOT NULL DEFAULT 10,
    activo       BOOLEAN       NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_producto_precio       CHECK (precio > 0),
    CONSTRAINT ck_producto_stock_minimo CHECK (stock_minimo >= 0)
) ENGINE=InnoDB;


CREATE TABLE existencia (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    bodega_id   BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad    INT    NOT NULL DEFAULT 0,
    version     INT    NOT NULL DEFAULT 0,
    CONSTRAINT uq_existencia UNIQUE (bodega_id, producto_id),
    CONSTRAINT ck_existencia_cantidad CHECK (cantidad >= 0),
    CONSTRAINT fk_exist_bodega   FOREIGN KEY (bodega_id)   REFERENCES bodega(id),
    CONSTRAINT fk_exist_producto FOREIGN KEY (producto_id) REFERENCES producto(id)
) ENGINE=InnoDB;


CREATE TABLE movimiento (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo              ENUM('ENTRADA','SALIDA','TRANSFERENCIA') NOT NULL,
    usuario_id        BIGINT NOT NULL,
    bodega_origen_id  BIGINT NULL,
    bodega_destino_id BIGINT NULL,
    proveedor_id      BIGINT NULL,
    observacion       VARCHAR(255),
    estado            ENUM('REGISTRADO','ANULADO') NOT NULL DEFAULT 'REGISTRADO',
    fecha_anulacion   DATETIME NULL,
    motivo_anulacion  VARCHAR(255) NULL,
    CONSTRAINT fk_mov_usuario   FOREIGN KEY (usuario_id)        REFERENCES usuario(id),
    CONSTRAINT fk_mov_origen    FOREIGN KEY (bodega_origen_id)  REFERENCES bodega(id),
    CONSTRAINT fk_mov_destino   FOREIGN KEY (bodega_destino_id) REFERENCES bodega(id),
    CONSTRAINT fk_mov_proveedor FOREIGN KEY (proveedor_id)      REFERENCES proveedor(id),
    CONSTRAINT ck_mov_bodegas CHECK (
        (tipo = 'ENTRADA'       AND bodega_origen_id IS NULL     AND bodega_destino_id IS NOT NULL) OR
        (tipo = 'SALIDA'        AND bodega_origen_id IS NOT NULL AND bodega_destino_id IS NULL) OR
        (tipo = 'TRANSFERENCIA' AND bodega_origen_id IS NOT NULL AND bodega_destino_id IS NOT NULL
                                AND bodega_origen_id <> bodega_destino_id)
    ),
    CONSTRAINT ck_mov_proveedor CHECK (proveedor_id IS NULL OR tipo = 'ENTRADA'),
    CONSTRAINT ck_mov_estado CHECK (
        (estado = 'REGISTRADO' AND fecha_anulacion IS NULL) OR
        (estado = 'ANULADO'    AND fecha_anulacion IS NOT NULL)
    )
) ENGINE=InnoDB;

CREATE INDEX idx_mov_fecha  ON movimiento(fecha);
CREATE INDEX idx_mov_estado ON movimiento(estado);

CREATE TABLE movimiento_item (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    movimiento_id   BIGINT        NOT NULL,
    producto_id     BIGINT        NOT NULL,
    cantidad        INT           NOT NULL,
    precio_unitario DECIMAL(12,2) NOT NULL,
    CONSTRAINT uq_mov_item UNIQUE (movimiento_id, producto_id),
    CONSTRAINT ck_item_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_item_precio   CHECK (precio_unitario > 0),
    CONSTRAINT fk_item_movimiento FOREIGN KEY (movimiento_id) REFERENCES movimiento(id),
    CONSTRAINT fk_item_producto   FOREIGN KEY (producto_id)   REFERENCES producto(id)
) ENGINE=InnoDB;


CREATE TABLE bitacora_cambios (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    operacion          ENUM('INSERTAR','ACTUALIZAR','BORRAR') NOT NULL,
    fecha_hora         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario            VARCHAR(50) NOT NULL,
    entidad            VARCHAR(50) NOT NULL,
    entidad_id         BIGINT NULL,
    valores_anteriores JSON NULL,
    valores_nuevos     JSON NULL
) ENGINE=InnoDB;

CREATE INDEX idx_bit_usuario   ON bitacora_cambios(usuario);
CREATE INDEX idx_bit_operacion ON bitacora_cambios(operacion);
CREATE INDEX idx_bit_fecha     ON bitacora_cambios(fecha_hora);
CREATE INDEX idx_bit_entidad   ON bitacora_cambios(entidad, entidad_id);


CREATE VIEW v_inventario_total AS
SELECT p.id           AS producto_id,
       p.sku          AS sku,
       p.nombre       AS nombre,
       p.categoria    AS categoria,
       p.precio       AS precio,
       p.stock_minimo AS stock_minimo,
       p.activo       AS activo,
       COALESCE(SUM(e.cantidad), 0) AS stock_total,
       (COALESCE(SUM(e.cantidad), 0) < p.stock_minimo) AS bajo_stock
FROM producto p
LEFT JOIN existencia e ON e.producto_id = p.id
GROUP BY p.id, p.sku, p.nombre, p.categoria, p.precio, p.stock_minimo, p.activo;

CREATE VIEW v_inventario_por_bodega AS
SELECT b.id     AS bodega_id,
       b.nombre AS bodega,
       p.id     AS producto_id,
       p.nombre AS producto,
       e.cantidad AS cantidad
FROM existencia e
JOIN bodega   b ON b.id = e.bodega_id
JOIN producto p ON p.id = e.producto_id;

CREATE VIEW v_ocupacion_bodega AS
SELECT b.id        AS bodega_id,
       b.nombre    AS bodega,
       b.capacidad AS capacidad,
       COALESCE(SUM(e.cantidad), 0) AS unidades_almacenadas,
       b.capacidad - COALESCE(SUM(e.cantidad), 0) AS espacio_disponible,
       ROUND(COALESCE(SUM(e.cantidad), 0) * 100 / b.capacidad, 2) AS porcentaje_ocupacion
FROM bodega b
LEFT JOIN existencia e ON e.bodega_id = b.id
GROUP BY b.id, b.nombre, b.capacidad;


CREATE VIEW v_ranking_movimientos AS
SELECT p.id     AS producto_id,
       p.nombre AS nombre,
       COUNT(mi.id)      AS total_movimientos,
       SUM(mi.cantidad)  AS unidades_movidas
FROM movimiento_item mi
JOIN movimiento m ON m.id = mi.movimiento_id AND m.estado = 'REGISTRADO'
JOIN producto   p ON p.id = mi.producto_id
GROUP BY p.id, p.nombre
ORDER BY unidades_movidas DESC;



INSERT INTO municipio (nombre, departamento) VALUES
('Bogotá',   'Cundinamarca'),
('Medellín', 'Antioquia'),
('Itagüí',   'Antioquia'),
('Cali',     'Valle del Cauca'),
('Yumbo',    'Valle del Cauca');


INSERT INTO usuario (nombre, username, password, rol, estado_cuenta, aprobado_por_id, fecha_aprobacion) VALUES
('Administrador General', 'admin',     '$2a$10$OGC2gKsSQN38wgsk7d0BreFWF0ajsfIDpxYlKX3KaSdpueiaV3LiC', 'ADMIN',    'ACTIVA',    NULL, NULL),
('Empleado de Prueba',    'empleado1', '$2a$10$JGH5cMXiCAcCovTDR8MC1.qgeRxuP27v0oeNuee3EPppKc6p9SbZO', 'EMPLEADO', 'ACTIVA',    1,    '2026-10-30 09:00:00'),
('Pedro Solicitante',     'pedro',     '$2a$10$7fvf5xOuyuLGHAXJ2AHO7uJxbU/43ff8aJT585MO3S9xkLe2MIe2u', 'EMPLEADO', 'PENDIENTE', NULL, NULL);

INSERT INTO proveedor (nombre, nit, telefono, correo, municipio_id) VALUES
('Distribuidora Tecnológica SAS', '900123456-1', '6015551234', 'ventas@distritec.com',   1),
('Papelería y Más Ltda',          '800987654-3', '6045555678', 'contacto@papeleriaymas.co', 2);

INSERT INTO bodega (nombre, ubicacion, municipio_id, capacidad, encargado) VALUES
('Bodega Central',   'Zona Industrial', 1, 5000, 'Carlos Ramírez'),
('Bodega Norte',     'Sector Itagüí',   3, 3000, 'Laura Gómez'),
('Bodega Occidente', 'Zona Franca',     5, 2500, 'Andrés Torres');

INSERT INTO producto (sku, nombre, categoria, precio) VALUES
('TEC-001', 'Laptop Lenovo IdeaPad', 'Tecnología', 2500000.00),
('TEC-002', 'Mouse inalámbrico',     'Tecnología',   45000.00),
('TEC-003', 'Teclado mecánico',      'Tecnología',  180000.00),
('MUE-001', 'Silla ergonómica',      'Muebles',     650000.00),
('MUE-002', 'Escritorio de oficina', 'Muebles',     480000.00),
('PAP-001', 'Resma de papel carta',  'Papelería',    14000.00),
('TEC-004', 'Impresora láser',       'Tecnología',  900000.00);

INSERT INTO existencia (bodega_id, producto_id, cantidad) VALUES
(1, 1, 15), (2, 1, 10),
(1, 2,  5), (3, 2,  3),
(1, 3, 20), (2, 3, 10), (3, 3, 10),
(2, 4,  6),
(2, 5,  5), (3, 5, 10),
(1, 6, 60), (2, 6, 40), (3, 6, 20),
(1, 7,  9);

INSERT INTO movimiento (fecha, tipo, usuario_id, bodega_origen_id, bodega_destino_id, proveedor_id, observacion, estado, fecha_anulacion, motivo_anulacion) VALUES
('2026-11-02 09:15:00', 'ENTRADA',       2, NULL, 1,    1,    'Ingreso de proveedor',        'REGISTRADO', NULL, NULL),
('2026-11-05 14:30:00', 'SALIDA',        2, 2,    NULL, NULL, 'Despacho a cliente',          'REGISTRADO', NULL, NULL),
('2026-11-09 11:00:00', 'TRANSFERENCIA', 1, 1,    3,    NULL, 'Reabastecimiento Occidente',  'REGISTRADO', NULL, NULL),
('2026-11-10 15:00:00', 'SALIDA',        2, 1,    NULL, NULL, 'Despacho duplicado',          'ANULADO', '2026-11-10 16:00:00', 'Registrado por error');

INSERT INTO movimiento_item (movimiento_id, producto_id, cantidad, precio_unitario) VALUES
(1, 3, 10,  180000.00), (1, 6, 20,   14000.00),
(2, 1,  2, 2500000.00), (2, 5,  1,  480000.00),
(3, 6, 10,   14000.00), (3, 2,  3,   45000.00),
(4, 2,  1,   45000.00);

SET FOREIGN_KEY_CHECKS = 1;