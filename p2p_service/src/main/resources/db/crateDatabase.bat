psql -U postgres -h localhost -tc "SELECT 1 FROM pg_database WHERE datname = 'p2p_trading_dev'" | grep -q 1 || psql -U postgres -h localhost -c "CREATE DATABASE p2p_trading_dev OWNER postgres;"
mvn flyway:migrate
mvn spring-boot:run
