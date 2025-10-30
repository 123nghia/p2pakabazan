# P2P Trading System - Tai lieu Kien truc va Huong dan

Tai lieu nay dung lam co so chung de thanh vien nhom hieu nhanh ve he thong, cau truc module va cac huong dan phat trien quan trong.

## 1. Tong quan he thong
- Mo ta: Nen tang giao dich P2P ho tro dang quang cao mua ban, ghep lenh va xu ly tranh chap theo thoi gian gan thuc.
- Doi tuong su dung: Nguoi dung P2P (trader), nhan vien ho tro, he thong tich hop ben ngoai.
- Chuc nang chinh: Quan ly order/trade, dong bo thi truong Binance, chat giao dich, xu ly tranh chap, thong bao su kien, quan tri admin.
- Cong nghe trung tam: Java 17, Spring Boot 3, Spring Data JPA, Spring Security + JWT, PostgreSQL, RabbitMQ, Flyway, Maven multi-module.

## 2. Cau truc module Maven
- `p2p_common`: Dinh nghia constant, dto chung, util, base exception.
- `p2p_repository`: Entity, Repository JPA, Flyway migration (`classpath:db/migration`), mapping sang PostgreSQL.
- `p2p_service`: Business service, Command/Result pattern, transaction boundary, tich hop Binance, quan ly so du, xu ly tranh chap.
- `p2p_security`: Cau hinh Spring Security, JwtAuthenticationFilter, SecretKey, SecurityFilterChain, annotation ho tro phan quyen.
- `p2p_p2p`: Spring Boot API public (`/api/**`), controller REST, mapper DTO, swagger config, tich hop RabbitMQ.
- `p2p_notification`: Nhan, luu va phan phoi thong bao (NotificationService, type enum, giao tiep sang messaging).
- `p2p_scheduler`: Job theo lich (cron, queue sync), xu ly tiep cnh order/trade hoac dong bo thong tin.
- `p2p_admin`: Ung dung Spring Boot rieng cho quan tri (API / UI ho tro thao tac admin noi bo).

Chu y: `pom.xml` root dong vai tro BOM dieu phoi dependency va build chain; su dung `mvn -pl <module> -am` de build chinh xac module can test.

## 3. Dong chay chinh trong he thong
1. `Client -> API`: Request di vao `p2p_p2p` qua servlet path `/api`. Swagger UI tai `/api/swagger-ui/index.html`.
2. `Security`: JwtAuthenticationFilter trong `p2p_security` kiem tra token, nap Principal vao SecurityContext. Endpoint public duoc khai bao rieng.
3. `Controller`: Map payload -> `*Command` (mapstruct hoac mapper thu cong), goi service tuong ung trong `p2p_service`.
4. `Service`: Transactional (annotated `@Transactional`), goi repository, quan ly lock so du, lien he RabbitMQ hoac Binance neu can.
5. `Repository`: Lam viec voi PostgreSQL, Flyway dam bao schema san sang truoc khi khoi dong.
6. `Notification/Scheduler`: Thong bao duoc day qua RabbitMQ hoac NotificationService; scheduler chay job dinh ky (dong bo, cleanup).
7. `Integration`: Endpoint `/api/integration/users/sync` nhan du lieu tu he thong khac, tao/sua user va cap token.

## 4. Ha tang va cau hinh
- Profile: mac dinh `local` (`spring.profiles.active` co the override bang env `SPRING_PROFILES_ACTIVE`). Co `dev`, `prod` cho moi truong khac.
- Bien moi truong quan trong:
  - Database: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_POOL_SIZE`.
  - Security: `JWT_SECRET` (yeu cau >= 32 ky tu), `JWT_EXPIRATION` (neu khai bao).
  - RabbitMQ: `SPRING_RABBITMQ_HOST`, `SPRING_RABBITMQ_PORT`, `SPRING_RABBITMQ_USER`, `SPRING_RABBITMQ_PASS`.
- Phu thuoc ngoai: PostgreSQL 14+, RabbitMQ, API Binance P2P (HTTP, gzip).
- Docker: `docker-compose.yml` cung cap service `db` va `app`; Dockerfile dung multi-stage build Maven -> JRE.
- Flyway: `spring.flyway.enabled=true`, `baseline-on-migrate=true`; giu `spring.jpa.hibernate.ddl-auto=none` tranh xung dot schema.

## 5. Huong dan phat trien (Guideline)
- **Clean module boundary**: Chi su dung class chung qua `p2p_common`; khong import truc tiep giua cac module service/repository/notification neu khong qua interface/public facade.
- **Command/Result pattern**: Controller -> Command DTO -> Service -> Result DTO -> Response. Tra ve Response DTO ro rang, tranh tra Entity truc tiep.
- **Transaction boundary**: Danh dau `@Transactional` tai service xu ly business hoan chinh (create trade, confirm payment...). Tranh su dung trong controller.
- **Exception**: Nem `ApplicationException` (hoac subclass) kem `ErrorCode` de mapping sang HTTP. Dinh nghia ErrorCode trong `p2p_common`.
- **Validation**: Su dung `jakarta.validation` (@Valid, @NotNull...) tren payload. Validate them logic trong service neu can.
- **Security**: Bat buoc su dung cac annotation phan quyen (VD `@Secured`, `@PreAuthorize`) tu `p2p_security`. Token phai duoc check o filter, tuyet doi khong tao bypass trong controller.
- **Messaging**: Khi phat su kien RabbitMQ, dat ten exchange/routing-key theo `application.properties`, bo sung suffix `.events` de de quan ly. Xu ly idempotent o consumer.
- **Logging**: Bat `app.request-logging.enabled` o local de debug; tren prod chi bat khi can. Log business quan trong o level INFO, su kien bat thuong o WARN/ERROR.
- **Coding style**: Theo Java Code Conventions, su dung Lombok tiet che (chi cho DTO, entity). Comment ngan gon truoc khoi logic phuc tap (locking, external call).
- **Pull request**: Giu commit nho gon, co test kem theo. Review chu y regression security va concurrency.

## 6. Kiem thu va dam bao chat luong
- Unit test: Dat tai `src/test/java` tung module. Service test voi mock repository, notification.
- Integration test: Su dung Testcontainers (PostgreSQL, RabbitMQ) de kiem chung transaction va messaging. Nen tao profile `test` rieng.
- API test: Su dung `MockMvc` hoac Postman Collection. Xac nhan swagger khong bi vo.
- Scheduler/Notification: Viet test phu simulation de dam bao job chay dung lich, thong bao danh dau read/chua read dung logic.
- Flyway: moi migration phai di kem test doc lap, dam bao co the rollback (thong qua script hoac mo ta). 

## 7. Van hanh va trien khai
- Monitoring: Nen them Spring Boot Actuator, health check `/actuator/health`, metrics cho DB va queue.
- Deployment: Build bang `mvn clean package` hoac Docker multi-stage. Su dung `SPRING_PROFILES_ACTIVE=prod` va env tuong ung khi chay container.
- Backup: Cau hinh backup PostgreSQL hang ngay, RabbitMQ message co the su dung quorum/ha cluster neu can.
- Incident response: Log error duoc day vao central logging (ELK, Loki...). Lap plan rollback bang cach giu lai version jar truoc do va script Flyway rollback neu bat buoc.

## 8. Lien he va tai lieu bo sung
- `README.md`: Huong dan build/run nhanh.
- `docs/api-spec.md`: Mo ta REST API chi tiet.
- `docs/binance-p2p-market-service.md`: Chi tiet tich hop thi truong Binance.
- Vui long cap nhat tai lieu nay khi bo sung module, thay doi dong chay hoac quy tac coding.

