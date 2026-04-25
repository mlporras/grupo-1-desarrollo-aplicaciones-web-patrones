/*
  Script de creacion de base de datos para Ecommerce MVP
  Grupo 1 - Desarrollo de Aplicaciones Web con Patrones
  Este script crea el esquema, tablas, usuarios y carga datos de ejemplo.
*/

-- Seccion de administracion
DROP DATABASE IF EXISTS ecommerce_emprendedores;
DROP USER IF EXISTS 'usuario_prueba'@'%';
DROP USER IF EXISTS 'usuario_prueba'@'localhost';

-- Creacion del esquema
CREATE DATABASE ecommerce_emprendedores
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- Usuarios
CREATE USER 'usuario_prueba'@'%' IDENTIFIED BY 'Usuar1o_Clave.';
CREATE USER 'usuario_prueba'@'localhost' IDENTIFIED BY 'Usuar1o_Clave.';

-- Permisos
GRANT SELECT, INSERT, UPDATE, DELETE ON ecommerce_emprendedores.* TO 'usuario_prueba'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON ecommerce_emprendedores.* TO 'usuario_prueba'@'localhost';
FLUSH PRIVILEGES;

USE ecommerce_emprendedores;

-- ===================== TABLAS =====================

CREATE TABLE usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  correo VARCHAR(150) NOT NULL,
  contrasena VARCHAR(255) NOT NULL,
  telefono VARCHAR(20),
  direccion VARCHAR(255),
  rol ENUM('ADMIN','CLIENTE','COLABORADOR') NOT NULL DEFAULT 'CLIENTE',
  activo BOOLEAN DEFAULT TRUE,
  fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario),
  UNIQUE (correo),
  INDEX ndx_correo (correo)
) ENGINE = InnoDB;

-- HU15: Planes de suscripcion
CREATE TABLE plan (
  id_plan INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  precio_mensual DECIMAL(10,2) NOT NULL CHECK (precio_mensual > 0),
  descripcion TEXT,
  max_productos INT DEFAULT 50,
  max_colaboradores INT DEFAULT 2,
  incluye_reportes BOOLEAN DEFAULT FALSE,
  incluye_cupones BOOLEAN DEFAULT FALSE,
  activo BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (id_plan)
) ENGINE = InnoDB;

CREATE TABLE tienda (
  id_tienda INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  nombre_comercial VARCHAR(150) NOT NULL,
  descripcion TEXT,
  correo_contacto VARCHAR(150),
  telefono_contacto VARCHAR(20),
  moneda VARCHAR(10) DEFAULT 'CRC',
  activo BOOLEAN DEFAULT TRUE,
  id_plan INT NULL,
  estado_suscripcion ENUM('ACTIVA','SUSPENDIDA','PRUEBA') DEFAULT 'PRUEBA',
  fecha_vencimiento DATE NULL,
  PRIMARY KEY (id_tienda),
  UNIQUE (id_usuario),
  CONSTRAINT fk_tienda_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_tienda_plan FOREIGN KEY (id_plan) REFERENCES plan(id_plan)
) ENGINE = InnoDB;

CREATE TABLE categoria (
  id_categoria INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  descripcion VARCHAR(255),
  activo BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (id_categoria),
  UNIQUE (nombre)
) ENGINE = InnoDB;

CREATE TABLE producto (
  id_producto INT NOT NULL AUTO_INCREMENT,
  id_categoria INT NOT NULL,
  nombre VARCHAR(150) NOT NULL,
  descripcion TEXT,
  precio DECIMAL(10,2) NOT NULL CHECK (precio > 0),
  ruta_imagen VARCHAR(1024),
  activo BOOLEAN DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_producto),
  INDEX ndx_nombre_producto (nombre),
  CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
) ENGINE = InnoDB;

CREATE TABLE inventario (
  id_inventario INT NOT NULL AUTO_INCREMENT,
  id_producto INT NOT NULL,
  stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
  umbral_minimo INT DEFAULT 5,
  PRIMARY KEY (id_inventario),
  UNIQUE (id_producto),
  CONSTRAINT fk_inventario_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
) ENGINE = InnoDB;

CREATE TABLE metodo_pago (
  id_metodo_pago INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  descripcion VARCHAR(255),
  activo BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (id_metodo_pago)
) ENGINE = InnoDB;

CREATE TABLE zona_envio (
  id_zona_envio INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  descripcion VARCHAR(255),
  costo_envio DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK (costo_envio >= 0),
  activo BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (id_zona_envio)
) ENGINE = InnoDB;

CREATE TABLE carrito_item (
  id_carrito_item INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  id_producto INT NOT NULL,
  cantidad INT NOT NULL DEFAULT 1 CHECK (cantidad > 0),
  fecha_agregado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_carrito_item),
  UNIQUE (id_usuario, id_producto),
  CONSTRAINT fk_carrito_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_carrito_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
) ENGINE = InnoDB;

-- HU13: Cupones de descuento
CREATE TABLE cupon (
  id_cupon INT NOT NULL AUTO_INCREMENT,
  codigo VARCHAR(50) NOT NULL,
  tipo_descuento ENUM('PORCENTAJE','MONTO_FIJO') NOT NULL,
  valor DECIMAL(10,2) NOT NULL CHECK (valor > 0),
  fecha_inicio DATE NOT NULL,
  fecha_fin DATE NOT NULL,
  usos_maximos INT DEFAULT 0,
  usos_actuales INT DEFAULT 0,
  activo BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (id_cupon),
  UNIQUE (codigo)
) ENGINE = InnoDB;

CREATE TABLE pedido (
  id_pedido INT NOT NULL AUTO_INCREMENT,
  numero_pedido VARCHAR(20) NOT NULL,
  id_usuario INT NOT NULL,
  id_metodo_pago INT,
  id_zona_envio INT,
  id_cupon INT NULL,
  direccion_envio VARCHAR(255) NOT NULL,
  telefono_envio VARCHAR(20),
  subtotal DECIMAL(10,2) NOT NULL,
  costo_envio DECIMAL(10,2) DEFAULT 0.00,
  descuento DECIMAL(10,2) DEFAULT 0.00,
  total DECIMAL(10,2) NOT NULL,
  estado ENUM('PENDIENTE','CONFIRMADO','ENVIADO','ENTREGADO','CANCELADO') DEFAULT 'PENDIENTE',
  fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_pedido),
  UNIQUE (numero_pedido),
  INDEX ndx_estado (estado),
  CONSTRAINT fk_pedido_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_pedido_metodo FOREIGN KEY (id_metodo_pago) REFERENCES metodo_pago(id_metodo_pago),
  CONSTRAINT fk_pedido_zona FOREIGN KEY (id_zona_envio) REFERENCES zona_envio(id_zona_envio),
  CONSTRAINT fk_pedido_cupon FOREIGN KEY (id_cupon) REFERENCES cupon(id_cupon)
) ENGINE = InnoDB;

CREATE TABLE detalle_pedido (
  id_detalle INT NOT NULL AUTO_INCREMENT,
  id_pedido INT NOT NULL,
  id_producto INT NOT NULL,
  cantidad INT NOT NULL CHECK (cantidad > 0),
  precio_unitario DECIMAL(10,2) NOT NULL,
  subtotal DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (id_detalle),
  CONSTRAINT fk_detalle_pedido FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido),
  CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
) ENGINE = InnoDB;

-- HU2: Backup de configuracion de tienda
CREATE TABLE tienda_config_backup (
  id_backup INT NOT NULL AUTO_INCREMENT,
  id_tienda INT NOT NULL,
  nombre_comercial VARCHAR(150),
  descripcion TEXT,
  correo_contacto VARCHAR(150),
  telefono_contacto VARCHAR(20),
  moneda VARCHAR(10),
  fecha_backup TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_backup),
  CONSTRAINT fk_backup_tienda FOREIGN KEY (id_tienda) REFERENCES tienda(id_tienda)
) ENGINE = InnoDB;

-- HU7: Diseno personalizado de tienda
CREATE TABLE diseno_tienda (
  id_diseno INT NOT NULL AUTO_INCREMENT,
  id_tienda INT NOT NULL,
  plantilla VARCHAR(50) DEFAULT 'default',
  color_primario VARCHAR(7) DEFAULT '#1a1a2e',
  color_secundario VARCHAR(7) DEFAULT '#0f3460',
  color_acento VARCHAR(7) DEFAULT '#e2a03f',
  ruta_logo VARCHAR(1024),
  borrador BOOLEAN DEFAULT TRUE,
  fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_diseno),
  CONSTRAINT fk_diseno_tienda FOREIGN KEY (id_tienda) REFERENCES tienda(id_tienda)
) ENGINE = InnoDB;

-- HU14: Colaboradores de tienda
CREATE TABLE colaborador (
  id_colaborador INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  id_tienda INT NOT NULL,
  rol_colaborador ENUM('EDITOR','VIEWER') NOT NULL DEFAULT 'VIEWER',
  activo BOOLEAN DEFAULT TRUE,
  fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_colaborador),
  UNIQUE (id_usuario, id_tienda),
  CONSTRAINT fk_colaborador_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_colaborador_tienda FOREIGN KEY (id_tienda) REFERENCES tienda(id_tienda)
) ENGINE = InnoDB;

-- ===================== DATOS DE PRUEBA =====================

INSERT INTO plan (nombre, precio_mensual, descripcion, max_productos, max_colaboradores, incluye_reportes, incluye_cupones) VALUES
('Basico', 5000.00, 'Plan basico para emprendedores', 20, 1, FALSE, FALSE),
('Profesional', 15000.00, 'Plan profesional con reportes', 100, 3, TRUE, TRUE),
('Empresarial', 30000.00, 'Plan completo para empresas', 999, 10, TRUE, TRUE);

INSERT INTO usuario (nombre, correo, contrasena, rol) VALUES
('Admin Demo', 'admin@tienda.com', 'admin123', 'ADMIN'),
('Carlos Lopez', 'carlos@correo.com', 'cliente123', 'CLIENTE'),
('Maria Gomez', 'maria@correo.com', 'cliente123', 'CLIENTE');

INSERT INTO tienda (id_usuario, nombre_comercial, descripcion, correo_contacto, moneda, id_plan, estado_suscripcion) VALUES
(1, 'Mi Tienda Online', 'Tienda de productos variados para emprendedores', 'contacto@mitienda.com', 'CRC', 1, 'PRUEBA');

INSERT INTO categoria (nombre, descripcion) VALUES
('Electronica', 'Dispositivos y accesorios electronicos'),
('Ropa', 'Prendas de vestir y accesorios'),
('Hogar', 'Articulos para el hogar y decoracion'),
('Deportes', 'Equipamiento y ropa deportiva');

INSERT INTO producto (id_categoria, nombre, descripcion, precio, ruta_imagen) VALUES
(1, 'Audifonos Bluetooth', 'Audifonos inalambricos con cancelacion de ruido', 25000.00,
 'https://live.staticflickr.com/5637/23436667620_cde8d89c5c_w.jpg'),
(1, 'Cargador Portatil', 'Power bank de 10000mAh con carga rapida', 15000.00,
 'https://live.staticflickr.com/5729/30507063003_dbbd567c38_w.jpg'),
(2, 'Camiseta Basica', 'Camiseta de algodon 100% disponible en varios colores', 8500.00,
 'https://live.staticflickr.com/7015/6428975527_422a82a78e_w.jpg'),
(2, 'Gorra Deportiva', 'Gorra ajustable con proteccion UV', 6000.00,
 'https://live.staticflickr.com/8608/29776249963_0c9e1bb575_w.jpg'),
(3, 'Lampara LED', 'Lampara de escritorio con luz regulable', 12000.00,
 'https://live.staticflickr.com/65535/50257015477_190a545d5a_w.jpg'),
(4, 'Botella Deportiva', 'Botella termica de acero inoxidable 750ml', 9500.00,
 'https://live.staticflickr.com/65535/50060517487_1877b17ba1_w.jpg');

INSERT INTO inventario (id_producto, stock, umbral_minimo) VALUES
(1, 50, 5),
(2, 30, 5),
(3, 100, 10),
(4, 75, 10),
(5, 20, 3),
(6, 40, 5);

INSERT INTO metodo_pago (nombre, descripcion) VALUES
('Tarjeta de Credito (Demo)', 'Pago simulado con tarjeta de credito'),
('Transferencia Bancaria (Demo)', 'Pago simulado por transferencia'),
('Pago contra Entrega', 'Pago al momento de recibir el producto');

INSERT INTO zona_envio (nombre, descripcion, costo_envio) VALUES
('San Jose Centro', 'Zona metropolitana de San Jose', 1500.00),
('Gran Area Metropolitana', 'Heredia, Alajuela, Cartago cercano', 2500.00),
('Resto del Pais', 'Zonas fuera del GAM', 4000.00),
('Retiro en Tienda', 'Sin costo - retiro presencial', 0.00);

INSERT INTO carrito_item (id_usuario, id_producto, cantidad) VALUES
(2, 1, 2),
(2, 3, 1);

INSERT INTO pedido (numero_pedido, id_usuario, id_metodo_pago, id_zona_envio, direccion_envio, telefono_envio, subtotal, costo_envio, total, estado) VALUES
('PED-000001', 3, 1, 1, 'Avenida Central, San Jose', '8888-1234', 33500.00, 1500.00, 35000.00, 'CONFIRMADO');

INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 1, 25000.00, 25000.00),
(1, 3, 1, 8500.00, 8500.00);
