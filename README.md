# DocMind
🤖 AI-powered document search &amp; Q&amp;A using RAG. Built with Java 21, Spring Boot 4, Spring AI, Google Gemini, PostgreSQL &amp; pgvector. 📄 Upload documents, 🔍 semantic search, and 💬 get context-aware answers. Useful for enterprise knowledge bases, research, education &amp; technical documentation. 🚀

# 🧠 DocMind — AI Document Intelligence Platform

DocMind is an **AI-powered Retrieval-Augmented Generation (RAG) application** that allows users to upload PDF documents and ask questions about their content.

The application processes uploaded documents, splits them into meaningful chunks, generates vector embeddings using **Google Gemini**, stores those embeddings in **PostgreSQL with pgvector**, and retrieves the most relevant information to provide context-aware AI responses.

---

## 🚀 Features

* 📄 Upload and process PDF documents
* ✂️ Automatic document chunking
* 🧠 Generate embeddings using Google Gemini
* 🔎 Semantic similarity search using pgvector
* 💬 Context-aware question answering using RAG
* 🗄️ PostgreSQL-based vector storage
* 🐳 Dockerized PostgreSQL infrastructure
* 🔌 REST APIs for document ingestion and chat
* ⚡ Retrieval of relevant document context before AI response generation

---

## 🏗️ Architecture

```text
                         ┌─────────────────────┐
                         │       Client        │
                         │   Postman / UI      │
                         └──────────┬──────────┘
                                    │
                                    │ HTTP
                                    ▼
                         ┌─────────────────────┐
                         │    Spring Boot      │
                         │      Backend        │
                         └──────────┬──────────┘
                                    │
                   ┌────────────────┴────────────────┐
                   │                                 │
                   ▼                                 ▼
          ┌─────────────────┐              ┌─────────────────┐
          │ Document Upload │              │   Chat Request  │
          │    Controller   │              │    Controller   │
          └────────┬────────┘              └────────┬────────┘
                   │                                │
                   ▼                                ▼
          ┌─────────────────┐              ┌─────────────────┐
          │ PDF Processing  │              │    RAG Service  │
          │    Pipeline     │              │                 │
          └────────┬────────┘              └────────┬────────┘
                   │                                │
                   ▼                                ▼
          ┌─────────────────┐              ┌─────────────────┐
          │ Text Chunking   │              │ Semantic Search │
          └────────┬────────┘              └────────┬────────┘
                   │                                │
                   ▼                                │
          ┌─────────────────┐                        │
          │ Gemini          │                        │
          │ Embeddings      │                        │
          └────────┬────────┘                        │
                   │                                │
                   └───────────────┐                │
                                   ▼                │
                         ┌─────────────────────┐     │
                         │ PostgreSQL +        │◄────┘
                         │ pgvector            │
                         └──────────┬──────────┘
                                    │
                                    │ Relevant Chunks
                                    ▼
                         ┌─────────────────────┐
                         │   Gemini LLM        │
                         │ Response Generation │
                         └──────────┬──────────┘
                                    │
                                    ▼
                              AI Response
```

---

## 🔄 RAG Workflow

DocMind follows the following pipeline:

```text
             DOCUMENT INGESTION
                     │
                     ▼
                PDF Upload
                     │
                     ▼
              Text Extraction
                     │
                     ▼
               Text Chunking
                     │
                     ▼
           Generate Embeddings
              using Gemini
                     │
                     ▼
          Store Vectors in pgvector
                     │
                     │
              USER QUESTION
                     │
                     ▼
             Generate Query
                Embedding
                     │
                     ▼
          Similarity Search
              using pgvector
                     │
                     ▼
          Retrieve Relevant Chunks
                     │
                     ▼
          Add Context to Prompt
                     │
                     ▼
              Gemini LLM
                     │
                     ▼
              Final Answer
```

---

# 🛠️ Tech Stack

### Backend

* Java
* Spring Boot
* Spring AI
* Spring Web
* Spring Data JPA

### AI / RAG

* Google Gemini
* Embeddings
* Retrieval-Augmented Generation (RAG)
* Vector Similarity Search

### Database

* PostgreSQL
* pgvector

### Infrastructure

* Docker
* Docker Compose

### API Testing

* Postman

---

# 📁 Project Structure

```text
DocMind/
│
├── src/
│   ├── main/
│   │   │
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ishanknjr/
│   │   │           └── docmind/
│   │   │
│   │   │               ├── config/
│   │   │               │   ├── AppPropertiesConfig.java
│   │   │               │   └── ...
│   │   │               │
│   │   │               ├── controller/
│   │   │               │   ├── DocumentController.java
│   │   │               │   └── ChatController.java
│   │   │               │
│   │   │               ├── service/
│   │   │               │   ├── DocumentService.java
│   │   │               │   ├── RagService.java
│   │   │               │   └── ...
│   │   │               │
│   │   │               ├── repository/
│   │   │               │   └── ...
│   │   │               │
│   │   │               ├── model/
│   │   │               │   └── ...
│   │   │               │
│   │   │               └── DocMindApplication.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── ...
│   │
│   └── test/
│       └── java/
│
├── docker-compose.yml
├── pom.xml
├── README.md
└── .gitignore
```

> Update the package/class names above to exactly match your repository.

---

# ⚙️ How It Works

## 1. Upload Document

The user uploads a PDF through the document upload API.

```http
POST /api/v1/documents
Content-Type: multipart/form-data
```

The backend receives the PDF and starts the document processing pipeline.

---

## 2. Extract Text

The PDF is processed and its textual content is extracted.

```text
PDF
 ↓
Text Extraction
 ↓
Raw Document Text
```

---

## 3. Chunk the Document

Large documents are divided into smaller chunks.

```text
Large PDF
    ↓
Extracted Text
    ↓
Chunk 1
Chunk 2
Chunk 3
...
Chunk N
```

Chunking allows the retrieval system to search for relevant sections instead of sending the entire document to the LLM.

---

## 4. Generate Embeddings

Each chunk is converted into a numerical vector using Google Gemini embeddings.

```text
Text Chunk
     ↓
Gemini Embedding Model
     ↓
Vector Representation
```

These vectors capture the semantic meaning of the document content.

---

## 5. Store Embeddings

The generated embeddings are stored in PostgreSQL using the **pgvector extension**.

```text
PostgreSQL
      │
      ├── Document Content
      ├── Metadata
      └── Embedding Vector
```

---

# 🔎 Semantic Search

When a user asks a question:

```text
"What is the main objective of this document?"
```

DocMind converts the question into an embedding.

```text
User Question
      ↓
Gemini Embedding
      ↓
Query Vector
```

The query vector is compared against stored document vectors.

```text
Query Vector
      ↓
pgvector Similarity Search
      ↓
Most Relevant Chunks
```

The retrieved chunks are then provided to the LLM as context.

---

# 🤖 RAG Pipeline

The final prompt sent to the LLM conceptually looks like:

```text
Context:
[Relevant document chunk 1]

[Relevant document chunk 2]

[Relevant document chunk 3]


Question:
What is the main objective of this document?
```

The LLM generates the answer using the retrieved context.

```text
User Question
      +
Retrieved Context
      ↓
     Gemini
      ↓
Final Answer
```

This reduces the need for the model to rely solely on its pretrained knowledge.

---

# 🔌 API Endpoints

## Document Upload

```http
POST /api/v1/documents
```

Uploads and processes a PDF document.

### Request

```text
Content-Type: multipart/form-data
```

### Example

```text
file: document.pdf
```

---

## AI Chat

```http
POST /api/v1/chat
```

Allows the user to ask questions about the uploaded documents.

### Example Request

```json
{
  "message": "What is the main topic of this document?"
}
```

### Example Response

```json
{
  "response": "The document primarily discusses..."
}
```

> Update these request/response examples to match your actual DTOs.

---

# 🐘 PostgreSQL + pgvector

DocMind uses PostgreSQL as the primary persistence layer and **pgvector** for vector similarity search.

The Docker setup provides a PostgreSQL instance with pgvector enabled.

```text
Docker
  │
  ▼
PostgreSQL
  │
  ├── Relational Data
  │
  └── pgvector
         │
         └── Embeddings
```

---

# 🐳 Running PostgreSQL with Docker

Start the database:

```bash
docker compose up -d
```

Check running containers:

```bash
docker ps
```

Stop the containers:

```bash
docker compose down
```

---

# 🔐 Environment Variables

Create the required environment variables before running the application.

Example:

```env
DB_HOST=localhost
DB_PORT=5434
DB_NAME=dockmind
DB_USER=postgres
DB_PASSWORD=your_password

GEMINI_API_KEY=your_gemini_api_key
```

**Never commit your actual API keys or database passwords to GitHub.**

---

# ▶️ Running the Application

### 1. Clone the repository

```bash
git clone https://github.com/Ishankatlam/DocMind.git
```

### 2. Navigate into the project

```bash
cd DocMind
```

### 3. Start PostgreSQL

```bash
docker compose up -d
```

### 4. Configure environment variables

Set your database and Gemini configuration.

### 5. Run the application

Using Maven:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The application will start on the configured Spring Boot port.

---

# 🧪 Testing with Postman

### Document Upload

```text
POST http://localhost:8083/api/v1/documents
```

Select:

```text
Body
 → form-data
 → key: file
 → type: File
 → select PDF
```

### Chat

```text
POST http://localhost:8083/api/v1/chat
```

Example:

```json
{
  "message": "Summarize the uploaded document."
}
```

---

# 🧩 Core Components

## Document Controller

Responsible for exposing document-related REST endpoints.

```text
HTTP Request
     ↓
DocumentController
     ↓
Document Processing Service
```

---

## RAG Service

Responsible for the question-answering pipeline.

```text
User Question
      ↓
RagService
      ↓
Generate Query Embedding
      ↓
Vector Search
      ↓
Retrieve Context
      ↓
Build Prompt
      ↓
Gemini
      ↓
Response
```

---

## Vector Store

The vector store manages:

* document chunks
* embeddings
* metadata
* similarity search

DocMind uses PostgreSQL + pgvector for this purpose.

---

# 📊 Key Engineering Concepts

DocMind demonstrates practical implementation of:

* REST API development
* Spring Boot
* Spring AI
* RAG architecture
* Vector embeddings
* Semantic search
* Vector databases
* PostgreSQL
* pgvector
* PDF processing
* Document chunking
* Docker
* Environment-based configuration
* AI prompt construction

---

# 🔮 Future Improvements

Possible improvements include:

* 🔐 Authentication and authorization
* 👤 User-specific document collections
* 📚 Multiple document support
* 🗂️ Document management dashboard
* 💬 Conversation history
* ⚡ Redis caching
* 📈 Monitoring with Spring Boot Actuator
* 🔍 Hybrid keyword + vector search
* 📑 Source/citation references in responses
* 🚀 Cloud deployment
* 📊 RAG evaluation and retrieval metrics

---

# 🎯 Learning Outcomes

Through DocMind, the project demonstrates how a traditional Spring Boot backend can be integrated with modern AI infrastructure.

The complete pipeline connects:

```text
Spring Boot
     ↓
Spring AI
     ↓
Gemini
     ↓
Embeddings
     ↓
PostgreSQL
     ↓
pgvector
     ↓
Vector Retrieval
     ↓
RAG
     ↓
AI Response
```

---

# 👨‍💻 Author

**Ishan Katlam**

GitHub:
https://github.com/Ishankatlam

---

# ⭐ Project

If you find this project useful, consider giving the repository a ⭐ on GitHub.

```

### One thing I strongly recommend

Before putting this on GitHub, **replace anything that doesn't exactly match your implementation**—especially the package structure, API request/response JSON, port (`8083`), and environment variable names.

Your README will look much stronger if the architecture diagram and API documentation correspond **1:1 with the actual code**, because interviewers often open the GitHub repo and cross-check the README against the implementation.
```
