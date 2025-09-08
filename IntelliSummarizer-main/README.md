# 🧠 Research Assistant

A Spring Boot microservice for processing and analyzing content using the Gemini AI model (via Google's Generative Language API).

---

## 🚀 Features

- 🔹 **Summarization**: Transform input text into concise summaries  
- 🔹 **Topic Suggestions**: Generate related reading topics for deeper research  
- 🔹 **API-ready**: RESTful endpoint ready for integration  

---

## 🧰 Prerequisites

- JDK 17 (or newer)  
- Maven 3.8+  
- Get your valid from [Google Generative Language API key](https://makersuite.google.com/app/apikey)

---

## 🛠️ Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/Babul98/IntelliSummarizer
cd research-assistant
```

### 2. Set Up Secrets

Rename the example .env file and add your Gemini API key:
```
mv .env.example .env
```

Then edit the file to include:
```
export GOOGLE_API_KEY=YOUR_GOOGLE_API_KEY_HERE
```

Source the .env file:
```
source .env
```

### 3. Build & Run

```bash
mvn clean package
java -jar target/research-assistant-0.0.1-SNAPSHOT.jar
```

---

## 🧪 Usage

### API Endpoint

Send a POST request to:

```
POST http://localhost:8080/api/research/process
Content-Type: application/json
```

### Request Body

```json
{
  "operation": "summarize",   // or "suggest"
  "content": "Your text to process..."
}
```

### Sample Response

- For `"summarize"` request →  
  `"Here’s a concise summary of your content..."`

- For `"suggest"` request →  
  ```
  - Related Topic 1
  - Related Topic 2
  ...
  ```

---

## 🧩 Project Structure

```
src/
├── main/
│   ├── java/com/research/research_assistant/
│   │   ├── ResearchService.java       // Business logic & API interaction
│   │   ├── ResearchController.java    // REST endpoint
│   │   └── ResearchRequest.java       // Request payload model
│   └── resources/
│       ├── application.properties.example  // Config template
│       └── application.properties          // (ignored, holds secrets)
└── test/                                   // Unit tests
```

# Documentations
- Google ADK:
    - [Quickstart](https://google.github.io/adk-docs/get-started/quickstart/)
    - [Java Github](https://github.com/google/adk-java)
    - [Java Streaming](https://google.github.io/adk-docs/get-started/streaming/quickstart-streaming-java/#creating-live-audio-run-tool)
    - [Java Samples](https://github.com/google/adk-samples)
- Google A2A:
    - [Java Github](https://github.com/a2aproject/a2a-java?tab=readme-ov-file)
