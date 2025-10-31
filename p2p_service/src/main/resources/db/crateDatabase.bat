psql -U postgres -h localhost -tc "SELECT 1 FROM pg_database WHERE datname = 'p2p_trading'" | grep -q 1 || psql -U postgres -h localhost -c "CREATE DATABASE p2p_trading OWNER postgres;"
mvn -pl p2p_repository flyway:migrate
mvn spring-boot:run
