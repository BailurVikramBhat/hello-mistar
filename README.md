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

## 🧪 Sample Output

**Request:**

```
GET /greet?lang=HindiHello
```

**Response:**

```
नमस्ते विश्व
```

---

## 📌 Notes

- No external libraries used
- Ideal for demonstrating understanding of interfaces, reflection, decoupling
- Can be extended to support JSON output, hot plugin reloads, or Swagger integration

---

## 🧑‍💻 Author

**Vikram Bhat**  
Java Developer (in progress 👨‍🔧) | Automation QA (ex) | Backend Architect (manifesting ✨)

---

> A Hello World project… but _make it backend-ready_.
