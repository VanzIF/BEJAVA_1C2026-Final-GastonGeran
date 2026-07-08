# Ecommerce API

Proyecto final de Back-End Java — Talento Tech, Comisión 26138.

API REST hecha con Spring Boot y MySQL para manejar un catálogo simple de
e-commerce: categorías, productos y órdenes de compra.

## De qué se trata

Elegí un e-commerce porque es un dominio que se presta bien para mostrar
relaciones entre entidades: cada producto pertenece a una categoría
(`@ManyToOne`), y una orden puede tener varios productos, mientras que un
producto puede aparecer en varias órdenes (`@ManyToMany`).

Lo que más me interesaba resolver era la parte de negocio al armar una
orden: que no se pueda crear vacía, que si pedís un producto que no existe
te avise, y sobre todo que controle el stock disponible antes de confirmar
la compra (y que se descuente automáticamente). Si cancelás una orden, el
stock se repone.

También agregué:
- Validaciones con Hibernate Validator en los campos de las entidades.
- Excepciones propias (`ResourceNotFoundException`, `InsufficientStockException`,
  `InvalidOrderException`, `DuplicateResourceException`) manejadas todas
  desde un solo lugar con `@RestControllerAdvice`, para no repetir
  try/catch en cada controller.
- CORS habilitado para poder pegarle desde cualquier front.

## Organización

```
model/        Category, Product, Order, OrderStatus
repository/   interfaces de Spring Data JPA
service/      la logica de negocio (validaciones, calculo de stock, etc)
controller/   los endpoints REST
dto/          lo que recibe/devuelve el endpoint de ordenes
exception/    excepciones propias + el manejador global
config/       CORS y la carga de datos de prueba
```

## Cómo correrlo

Necesitás Java 17+ y MySQL corriendo (o podés usar el perfil `h2` si no
querés instalar nada, más abajo explico).

1. Crear la base (o dejar que Spring la cree sola, ya está configurado
   `createDatabaseIfNotExist=true`):
   ```sql
   CREATE DATABASE ecommerce_db;
   ```
2. Si tu usuario/contraseña de MySQL no es `root`/`root`, cambialo en
   `src/main/resources/application.properties`.
3. Levantar la app:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Queda escuchando en `http://localhost:8080`.

### Sin instalar MySQL

Para probar rápido sin configurar nada, dejé un perfil con base en memoria:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

Tiene consola web en `http://localhost:8080/h2-console` (JDBC URL
`jdbc:h2:mem:ecommerce_db`, usuario `sa`, sin contraseña) por si querés
mirar las tablas.

### Tests

```bash
./mvnw test
```

## Datos de prueba

Al arrancar por primera vez se cargan solas 3 categorías y 5 productos
(ver `DataInitializer`), así se puede probar todo sin tener que dar de
alta nada a mano.

## Probar los endpoints

Categorías:
```bash
curl http://localhost:8080/api/categories

curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name":"Deportes","description":"Articulos deportivos"}'
```

Productos:
```bash
curl http://localhost:8080/api/products
curl "http://localhost:8080/api/products?categoryId=1"

curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
        "name": "Mouse inalambrico",
        "description": "Mouse ergonomico 2.4GHz",
        "price": 15000.00,
        "stock": 40,
        "category": {"id": 1}
      }'
```

Órdenes (si repetís un id en `productIds` cuenta como más de una unidad):
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
        "customerName": "Juan Perez",
        "customerEmail": "juan@example.com",
        "productIds": [1, 3, 3]
      }'

curl -X PUT http://localhost:8080/api/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "COMPLETED"}'
```

Si pedís más stock del disponible, la API responde 409 con un mensaje
claro en vez de un error genérico:
```json
{
  "timestamp": "2026-07-08T12:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Stock insuficiente para el producto 'Auriculares Bluetooth'. Disponible: 25, solicitado: 30",
  "path": "/api/orders"
}
```

## Endpoints

| Método | Endpoint                  | Qué hace                          |
|--------|----------------------------|-------------------------------------|
| GET    | /api/categories             | listar categorías                   |
| GET    | /api/categories/{id}        | ver una categoría                   |
| POST   | /api/categories              | crear categoría                     |
| PUT    | /api/categories/{id}        | editar categoría                    |
| DELETE | /api/categories/{id}        | borrar categoría                    |
| GET    | /api/products                | listar productos (`?categoryId=`)   |
| GET    | /api/products/{id}          | ver un producto                     |
| POST   | /api/products                | crear producto                      |
| PUT    | /api/products/{id}          | editar producto                     |
| DELETE | /api/products/{id}          | borrar producto                     |
| GET    | /api/orders                  | listar órdenes                      |
| GET    | /api/orders/{id}            | ver una orden                       |
| POST   | /api/orders                  | crear orden (valida stock)          |
| PUT    | /api/orders/{id}/status     | cambiar estado de una orden         |
| DELETE | /api/orders/{id}            | borrar orden                        |

## Stack

Java 17, Spring Boot 3 (Web, Data JPA, Validation), MySQL, H2 (para pruebas
locales), Maven.
