# MediSync Local MySQL Setup

The backend now defaults to the `local` profile, which uses persistent MySQL storage and
Flyway migrations. The `dev` profile still uses an in-memory H2 database for automated tests.

## 1. Install and start MySQL or MariaDB

On Debian, Ubuntu, or Kali Linux:

```bash
sudo apt update
sudo apt install mariadb-server
sudo systemctl enable --now mariadb
```

If your distribution provides MySQL instead:

```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl enable --now mysql
```

## 2. Create the local database and application user

Open the database shell:

```bash
sudo mariadb
```

Run:

```sql
CREATE DATABASE IF NOT EXISTS medisync_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'medisync'@'localhost'
  IDENTIFIED BY 'medisync_dev_password';

ALTER USER 'medisync'@'localhost'
  IDENTIFIED BY 'medisync_dev_password';

GRANT ALL PRIVILEGES ON medisync_db.* TO 'medisync'@'localhost';
FLUSH PRIVILEGES;
```

Exit:

```sql
EXIT;
```

## 3. Start the backend

The `local` profile is the default:

```bash
./mvnw spring-boot:run
```

To select it explicitly:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Flyway creates the schema, planning extensions, persisted monthly financial reports, and local
development accounts automatically on the first start. Existing databases are upgraded with
versioned migrations without schema recreation.

## 4. Optional environment overrides

The local defaults are suitable only for development. Override them when needed:

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=medisync_db
export DB_USERNAME=medisync
export DB_PASSWORD=medisync_dev_password
export JWT_SECRET='replace-with-a-long-random-secret-before-deployment'
```

Production uses the `prod` profile and does not load local seed accounts:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## 5. Local development accounts

These accounts exist only when Flyway runs with the `local` profile:

| Role | Email | Password |
| --- | --- | --- |
| Admin | `admin@medisync.local` | `Admin123!` |
| Doctor | `doctor@medisync.local` | `Doctor123!` |
| Secretary | `secretary@medisync.local` | `Secretary123!` |

The migration stores BCrypt hashes, not plain-text passwords. The plain-text credentials are
documented here only for local testing.

## 6. Verify persistence

Start the backend, log in, stop it with `Ctrl+C`, then start it again. Verify that the account
still exists:

```bash
curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@medisync.local","password":"Admin123!"}'
```

Inspect Flyway history and users:

```bash
mariadb -u medisync -p medisync_db
```

```sql
SELECT installed_rank, version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;

SELECT id, email, user_role
FROM users
ORDER BY id;

SELECT report_month, total_revenue, generated_at
FROM monthly_financial_reports
ORDER BY report_month DESC;
```

## 7. Fast automated tests

Tests continue to use disposable H2 storage:

```bash
./mvnw test -Dspring.profiles.active=dev
```
