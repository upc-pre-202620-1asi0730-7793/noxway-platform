-- Ejecutar una vez desde MySQL Workbench con una cuenta autorizada.
-- No elimina datos ni guarda contraseñas.
CREATE DATABASE IF NOT EXISTS noxway
  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE noxway;
-- Las tablas las crea Flyway al arrancar la aplicación.
