# P2P Trading System - Backend

## 📌 Giới thiệu
Đây là hệ thống backend cho ứng dụng **P2P Trading System**, được xây dựng bằng **Spring Boot (multi-module)**, kết nối **PostgreSQL**, hỗ trợ **JWT Authentication** và cung cấp RESTful API.

Cấu trúc dự án được tổ chức theo dạng **multi-module Maven**:
- `p2p_common` : chứa các class, constant, DTO, util dùng chung.
- `p2p_repository` : quản lý entity, repository, kết nối database.
- `p2p_service` : chứa business logic, service layer.
- `p2p_security` : quản lý xác thực, JWT, filter, config security.
- `p2p_p2p` : module chính khởi động Spring Boot (`main class`), expose API.

---

## 🏗️ Công nghệ sử dụng
- **Java 17+**
- **Spring Boot 3+**
- **Spring Data JPA (Hibernate)**
- **Spring Security + JWT**
- **PostgreSQL**
- **Maven Multi-Module**
- **Docker (optional)**

---

## ⚙️ Cấu hình

File cấu hình chính nằm ở `p2p_p2p/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/p2p_trading
spring.datasource.username=postgres
spring.datasource.password=123
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

jwt.secret=mysupersecuresecretkey_which_is_at_least_32_chars

spring.mvc.servlet.path=/api
