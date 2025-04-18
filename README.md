# 🌍 Hello World Plugin-Based REST API (Pure Java)

A modular and extensible Hello World server written in **pure Java** (no frameworks).  
Designed to demonstrate concepts like **plugin architecture**, **strategy pattern**, and **simple REST APIs** — all in the context of a fun and familiar problem: saying Hello in different languages.

---

## 🛠️ Features

- 🧩 **Plugin System**: Add greetings in new languages by dropping `.class` files into a directory — no need to recompile the whole app.
- 🔁 **Strategy Pattern**: Each language implements a common interface (`HelloStrategy`) for clean decoupling.
- 🌐 **REST API** (via `com.sun.net.httpserver`):
  - `GET /greet?lang=Hindi` — returns greeting from specified plugin
  - `GET /languages` — lists all available language plugins
- 🔒 **Custom Logging**: Switchable logger injected via abstraction
- ⚙️ **No Spring, No External Dependencies** — just pure Java fun

---

## 📁 Directory Structure

```
project-root/
├── src/
│   ├── App.java
│   ├── AppLogger.java
│   ├── GreetingLogger.java
│   ├── HelloApiServer.java
│   ├── HelloWorldService.java
│   ├── HelloStrategy.java
│   ├── EnglishHello.java
│   └── HindiHello.java
├── plugins/
│   ├── SpanishHello.class
│   └── Other plugins...
```

---

## 🚀 How to Run

### 1. Compile Java files

```bash
javac -d bin src/*.java
```

### 2. Run the server

```bash
java -cp bin HelloApiServer
```

### 3. Access Endpoints

- `http://localhost:8000/greet?lang=EnglishHello`
- `http://localhost:8000/languages`

## Make sure compiled plugin `.class` files exist in the top-level `plugins/` directory.

## ➕ Adding a New Language Plugin

1. Implement `HelloStrategy` in a new class:

```java
public class FrenchHello implements HelloStrategy {
    public String sayHello() {
        return "Bonjour le monde";
    }
}
```

2. Compile it separately:

```bash
javac -d plugins src/FrenchHello.java
```

3. That’s it! Access via `/greet?lang=FrenchHello`.

---

## 📈 New Features Added

### ✅ `/greet` now returns JSON

Returns a structured response with language, name, and greeting:

```json
{
  "language": "HindiHello",
  "name": "Vikram",
  "greeting": "नमस्ते Vikram"
}
```

---

### ✅ `/health` endpoint

Returns system status with memory usage, uptime, and build metadata.

```json
{
  "status": "UP",
  "plugins": 3,
  "timestamp": "18-04-2025 - 22:45:10",
  "uptime": "0h 12m 36s",
  "memory": {
    "used": "35 MB",
    "free": "120 MB",
    "total": "160 MB"
  },
  "version": "1.0.0",
  "build": "2025-04-18 22:33:00"
}
```

- ✅ Auto-generated `build.properties` using script
- ✅ Metadata read dynamically from file

---

### ✅ `/history` endpoint (file-based logging)

Stores every greeting request with timestamp. Returns JSON array of greeting records:

```json
[
  {
    "language": "SpanishHello",
    "name": "Vikram",
    "greeting": "¡Hola Vikram!",
    "timestamp": "18-04-2025 - 22:32:00"
  }
]
```

- Logged to `history.log` file in root
- Persistent across app restarts

---

### ✅ `/metrics` endpoint

Counts the number of times each plugin/language was used:

```json
{
  "EnglishHello": 4,
  "HindiHello": 2,
  "SpanishHello": 3
}
```

- Calculated by scanning `history.log`
- Perfect for usage stats

---

---

## 📘 API Documentation (Swagger/OpenAPI)

This project includes Swagger (OpenAPI 3.0) documentation for all available endpoints.

The documentation is defined in a static `swagger.json` file and can be accessed from:

```
GET /docs
```

You can preview the API using [Swagger Editor](https://editor.swagger.io) or Swagger UI.

---

### 🔧 How to Preview in Swagger Editor

1. Run the app
2. Go to: [https://editor.swagger.io](https://editor.swagger.io)
3. Click **File → Import URL**
4. Paste: `http://localhost:8080/docs`

---

### 🗂️ Documented Endpoints

The Swagger JSON includes:

| Endpoint     | Description                      |
| ------------ | -------------------------------- |
| `/greet`     | Greets user in a chosen language |
| `/languages` | Lists available language plugins |
| `/history`   | Returns all greeting records     |
| `/metrics`   | Returns usage count per language |
| `/health`    | System health + uptime info      |

Example `/greet` schema in Swagger:

```json
{
  "language": "SpanishHello",
  "name": "Vikram",
  "greeting": "¡Hola Vikram!"
}
```

---

### 💡 Optional: Add Swagger UI in `/docs.html`

To make it even cooler:

- Download Swagger UI static files
- Serve them at `/docs.html`
- Have it fetch your `/docs` spec automatically

This makes your API visually interactive on the browser — just like a professional REST platform.

---

## 🧪 Pro Tip

Swagger docs also help you:

- Communicate API usage clearly
- Generate client SDKs for free
- Make your repo look like it belongs in production

---

## 📌 Notes

- No external libraries used
- Ideal for demonstrating understanding of interfaces, reflection, decoupling
- Can be extended to support JSON output, hot plugin reloads, or Swagger integration

---

---

## 🆙 Upgrade Notes

This section highlights major feature additions and backend enhancements made after the initial release of the Hello World Plugin API.

### 🔹 [v1.1.0] – April 18, 2025

**✅ Core Improvements**

- `/greet` endpoint now returns clean, structured JSON
- Log files formatted with `dd-MM-yyyy - HH:mm:ss` timestamps
- Added dynamic versioning and build info using `build.properties`

**✅ Monitoring & Observability**

- Introduced `/health` endpoint with:
  - Memory usage
  - Uptime tracking
  - Plugin count
  - Version and build metadata
- Health status auto-updated via shell/batch script at compile time

**✅ Persistence & History**

- All greeting requests are logged in `history.log`
- Introduced `/history` endpoint to return greeting records as JSON
- Persistent even after server restart

**✅ Analytics**

- `/metrics` endpoint added to show plugin usage counts from `history.log`
- Helps track how many times each language was used

**✅ Documentation**

- Swagger/OpenAPI 3.0 spec added via static `swagger.json`
- `/docs` endpoint serves live documentation
- Compatible with Swagger Editor or Swagger UI

---

### 📌 Planned for Next Update

- `/delete-history` endpoint
- Host Swagger UI at `/docs.html`
- Dockerfile for containerized deployments
- Per-language greeting analytics with timestamps

## 🧑‍💻 Author

**Vikram Bhat**  
Java Developer (in progress 👨‍🔧) | Automation QA (ex) | Backend Architect (manifesting ✨)

---

> A Hello World project… but _make it backend-ready_.
