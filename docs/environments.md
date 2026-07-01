# Environments

## Dev Test

Dev Test is for local Postman testing and fast debugging.

- Spring profile: `dev-test`
- Database: `rikkei_banking_dev`
- Hibernate: `update`
- SQL log: enabled
- Seed data: enabled

Seeded users:

| Username | Password | Role |
| --- | --- | --- |
| admin | 123456 | ROLE_ADMIN |
| staff | 123456 | ROLE_STAFF |
| customer1 | 123456 | ROLE_CUSTOMER |
| customer2 | 123456 | ROLE_CUSTOMER |

Seeded accounts:

| Owner | Account Number | PIN | Balance |
| --- | --- | --- | --- |
| customer1 | 1000000001 | 123456 | 10000000.00 |
| customer2 | 1000000002 | 123456 | 5000000.00 |

Run with Dev Test profile:

```powershell
.\gradlew.bat bootRun --args='--spring.profiles.active=dev-test'
```

Or keep the default in `application.properties`:

```properties
spring.profiles.active=dev-test
```

The Dev Test JDBC URL includes `createDatabaseIfNotExist=true`, so the app can create `rikkei_banking_dev` automatically when the MySQL user has permission.

Before a clean Dev Test run:

```sql
DROP DATABASE IF EXISTS rikkei_banking_dev;
CREATE DATABASE rikkei_banking_dev;
```

Then restart the app.

Import this Postman environment:

```text
docs/postman/RikkeiBanking-Dev-Test.postman_environment.json
```

## Pro

Pro is for SRS demo or production-like verification.

- Spring profile: `pro`
- Database: `rikkei_banking_pro`
- Hibernate: `validate`
- SQL log: disabled
- Seed data: disabled

Run with Pro profile:

```powershell
.\gradlew.bat bootRun --args='--spring.profiles.active=pro'
```

Create the database manually:

```sql
CREATE DATABASE rikkei_banking_pro;
```

Because `ddl-auto=validate`, tables must already exist before the app starts. Use Pro only after the schema is prepared.

Import this Postman environment:

```text
docs/postman/RikkeiBanking-Pro.postman_environment.json
```
