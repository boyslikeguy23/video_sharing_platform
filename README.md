# Video Sharing Platform backend

Spring Boot API for users, posts, comments, stories, and chat. Java 17 or newer and PostgreSQL are required. Docker is not needed for local development.

## Prepare PostgreSQL

Create an empty database named `video_sharing_website_db` in pgAdmin, or run this command in PowerShell (it prompts for the local `postgres` password):

```powershell
& 'C:\Program Files\PostgreSQL\18\bin\createdb.exe' -h localhost -p 5432 -U postgres -W video_sharing_website_db
```

Spring Boot's `ddl-auto=update` creates the **tables**, but PostgreSQL must already contain the **database**. If you use another database name, set `DB_NAME` before starting the app.

## Build and run in PowerShell

From this directory, point `JAVA_HOME` at a JDK. On the current development machine, IntelliJ's JDK is at the path shown below. Enter the PostgreSQL password when prompted; it stays in this PowerShell session and is not saved in the repository.

```powershell
$env:JAVA_HOME = 'C:\Users\MXTUNG\.jdks\openjdk-27'
$secureDbPassword = Read-Host -AsSecureString 'PostgreSQL password'
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $secureDbPassword).Password
.\scripts\Initialize-LocalJwt.ps1
.\mvnw.cmd clean package
& "$env:JAVA_HOME\bin\java.exe" -jar .\target\video_sharing_platform-0.0.1-SNAPSHOT.jar
```

The API listens at `http://localhost:5454`. Check `http://localhost:5454/actuator/health` for `{"status":"UP"}`. Stop it with Ctrl+C.

For IntelliJ, open this directory (the one containing `pom.xml`) as the project. IntelliJ will import it as a Maven project and keep its `.idea` settings in this directory. Select a JDK for the project module, then add `DB_PASSWORD` to the local `FinalProjectSpringBootApplication` run configuration's environment variables. Select that configuration and click Run.

Run `scripts/Initialize-LocalJwt.ps1` once before the first application start. It creates a random signing key in `.local/application.properties`, which Git ignores. Run the application with this project directory as its working directory so Spring can load that file. The script preserves an existing configuration. Alternatively, supply `JWT_SECRET` as an environment variable containing Base64 encoding of at least 32 cryptographically random bytes. An environment variable overrides the local file. Never commit or share the key. The application fails to start if the key is missing or invalid.

Access tokens expire after eight hours by default (`JWT_TTL=PT8H`). Changing the key invalidates previously issued tokens; sign in again after upgrading from the old hardcoded key.

Optional environment variables: `DB_HOST` (default `localhost`), `DB_PORT` (default `5432`), `DB_NAME` (default `video_sharing_website_db`), `DB_USERNAME` (default `postgres`), `SERVER_PORT` (default `5454`), `DB_DDL_AUTO` (default `update`), and `JPA_SHOW_SQL` (default `false`).

## Authorization rules

- Comment edit/delete endpoints require a Bearer token and allow only the comment author. A foreign author receives HTTP 403. Creating a comment ignores client-supplied IDs, author information, and likes.
- Marking a message as read is allowed only for its recipient, including the bulk read endpoint.
- Invalid or expired Bearer tokens receive HTTP 401. Sign in through the existing `GET /signin` Basic authentication endpoint; its `Authorization` response header contains the token.
- WebSocket STOMP `CONNECT` requires `Authorization: Bearer <token>`. Clients may send to `/app/chat.send` and `/app/chat.delete`, and subscribe to `/user/queue/messages` and `/user/queue/errors`. Direct broker destinations and other users' subscriptions are denied. Expired sessions cannot send or subscribe again; reconnect with a fresh token. Tokens are never written to authentication logs.

## Tests

Run the security regression tests without PostgreSQL:

```powershell
.\mvnw.cmd '-Dtest=JwtTokenProviderTest,SecurityRegressionTest,WebSocketAuthInterceptorTest' test
```

`mvnw.cmd clean package` also tests real HTTP login, comment ownership, read receipts, and private WebSocket delivery against PostgreSQL. Set `DB_PASSWORD` first, with the same optional database environment variables listed above. The test account must have permission to create schemas. Integration tests create a randomly named `security_test_*` schema and drop it afterwards; they do not write to the application's normal schema. Test signing keys are generated independently of the local application key.
