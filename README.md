# Gemini RAG Chatbot

An AI-powered document question-answering chatbot built using React, Spring Boot, Google Gemini API, and Qdrant Vector Database.

The application uses Retrieval-Augmented Generation (RAG) to retrieve relevant information from documents and provide context-aware answers using the Gemini API.

##  Table of Contents

- [Project Overview](#-project-overview)
- [Problem Statement](#-problem-statement)
- [Proposed Solution](#-proposed-solution)
- [Objectives](#-objectives)
- [Key Features](#-key-features)
- [System Architecture](#-system-architecture)
- [How It Works](#-how-it-works)
- [RAG Workflow](#-rag-workflow)
- [Technology Stack](#-technology-stack)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Getting Gemini API Key](#-getting-gemini-api-key)
- [Configuring Gemini API Key](#-configuring-gemini-api-key)
- [Running Spring Boot Backend](#-running-spring-boot-backend)
- [Running React Frontend](#-running-react-frontend)
- [Running the Complete Application](#-running-the-complete-application)
- [API Overview](#-api-overview)
- [Security](#-security)
- [GitHub Repositories](#-github-repositories)
- [Future Enhancements](#-future-enhancements)
- [Author](#-author)

---

#  Project Overview

The Gemini RAG Chatbot is an AI-powered document question-answering application.

The system allows users to interact with documents through a conversational chatbot interface.

Instead of relying only on the general knowledge of a Large Language Model, the application retrieves relevant information from documents and provides that information to Gemini as context.

This approach helps the chatbot generate responses that are more relevant to the provided documents.

---

#  Problem Statement

Traditional AI chatbots generate responses mainly based on the knowledge available to their underlying language model.

However, users may need to ask questions about their own documents or domain-specific information.

Examples include:

- PDF documents
- Company documents
- Project documentation
- Business information
- Technical documents
- Private or domain-specific content

A general-purpose chatbot may not have access to this information.

Therefore, there is a need for a system that can:

1. Accept user documents.
2. Process the document content.
3. Divide documents into manageable chunks.
4. Store information for efficient retrieval.
5. Retrieve relevant information based on a user's question.
6. Provide the retrieved information to an AI model.
7. Generate a meaningful answer based on the retrieved context.

---

#  Proposed Solution

The proposed solution is a Retrieval-Augmented Generation (RAG) chatbot.

The application combines:

- Document processing
- PDF text extraction
- Document chunking
- Vector database
- Similarity-based retrieval
- Google Gemini API
- Spring Boot REST APIs
- React frontend

When a user asks a question, the backend retrieves relevant information from the stored document data.

The retrieved information is then provided to Google Gemini as context.

Gemini generates the final response based on the retrieved context.

---

#  Objectives

The main objectives of this project are:

- Build an AI-powered document chatbot.
- Implement a Retrieval-Augmented Generation architecture.
- Allow users to interact with document content using natural language.
- Process PDF documents.
- Store and retrieve document information using Qdrant.
- Integrate Google Gemini for AI-generated responses.
- Develop REST APIs using Spring Boot.
- Build a user-friendly React frontend.
- Keep API credentials outside the source code.
- Use environment variables for sensitive configuration.
- Provide an extensible architecture for document-based question answering.

---

#  Key Features

##  Document Processing

- Upload documents through the application.
- Extract text from PDF documents.
- Divide document content into smaller chunks.
- Prepare document content for retrieval.

##  Information Retrieval

- Store document information in a vector database.
- Search for relevant information based on the user's question.
- Retrieve context related to the user's query.

##  AI Chatbot

- Google Gemini API integration.
- Natural-language question answering.
- Context-aware responses.
- Document-based question answering.

##  Web Application

- React frontend.
- Spring Boot backend.
- REST API communication.
- Interactive chatbot interface.

##  Security

- Gemini API key is not hardcoded in Java source code.
- API key is provided through an environment variable.
- Sensitive configuration files are excluded using `.gitignore`.

---

#  System Architecture

```text
                    ┌─────────────────────┐
                    │        User         │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │   React Frontend    │
                    └──────────┬──────────┘
                               │
                         REST API Calls
                               │
                               ▼
                    ┌─────────────────────┐
                    │  Spring Boot API    │
                    └──────────┬──────────┘
                               │
                ┌──────────────┴──────────────┐
                │                             │
                ▼                             ▼
      ┌──────────────────┐          ┌──────────────────┐
      │ Document Service │          │   Chat Service   │
      └────────┬─────────┘          └────────┬─────────┘
               │                             │
               ▼                             ▼
      ┌──────────────────┐          ┌──────────────────┐
      │ PDF Chunking     │          │ Retrieve Context │
      └────────┬─────────┘          └────────┬─────────┘
               │                             │
               ▼                             ▼
      ┌──────────────────────────────────────────────┐
      │              Qdrant Vector DB                │
      └──────────────────────┬───────────────────────┘
                             │
                      Relevant Context
                             │
                             ▼
                  ┌─────────────────────┐
                  │   Google Gemini     │
                  │       API           │
                  └──────────┬──────────┘
                             │
                       AI Response
                             │
                             ▼
                    ┌─────────────────────┐
                    │   React Frontend    │
                    └──────────┬──────────┘
                               │
                               ▼
                             User
```

---

#  How It Works

The application works in the following steps.

### Step 1 — User opens the application

The user opens the React frontend in a web browser.

### Step 2 — Document Upload

The user uploads a document through the chatbot application.

### Step 3 — Backend Receives the Document

The React frontend sends the document to the Spring Boot backend through a REST API.

### Step 4 — PDF Processing

The backend processes the PDF and extracts its text content.

### Step 5 — Document Chunking

The extracted text is divided into smaller chunks.

This allows the system to retrieve relevant portions of the document instead of processing the complete document every time.

### Step 6 — Vector Storage

The processed document information is stored in Qdrant.

### Step 7 — User Asks a Question

The user enters a question in the chatbot.

### Step 8 — Relevant Information Retrieval

The Spring Boot backend searches for relevant information from the vector database.

### Step 9 — Context Preparation

The retrieved information is prepared as context for the AI model.

### Step 10 — Gemini API

The context and user question are sent to Google Gemini.

### Step 11 — AI Response

Gemini generates a natural-language response.

### Step 12 — Response Display

The Spring Boot backend sends the response to the React frontend, where it is displayed to the user.

---

# RAG Workflow

The Retrieval-Augmented Generation workflow can be represented as:

```text
             DOCUMENT
                 │
                 ▼
        Document Processing
                 │
                 ▼
           Text Extraction
                 │
                 ▼
          Text Chunking
                 │
                 ▼
        Vector Representation
                 │
                 ▼
             QDRANT
                 │
                 │
          User Question
                 │
                 ▼
        Similarity Retrieval
                 │
                 ▼
       Relevant Document Context
                 │
                 ▼
          Google Gemini
                 │
                 ▼
         Generated Response
                 │
                 ▼
          React Frontend
                 │
                 ▼
               User
```

---

#  Technology Stack

## Frontend

- React
- Vite
- JavaScript
- HTML
- CSS

## Backend

- Java
- Spring Boot
- Maven
- REST APIs

## Artificial Intelligence

- Google Gemini API
- Retrieval-Augmented Generation (RAG)

## Vector Database

- Qdrant

## Development Tools

- Visual Studio Code
- Eclipse / Spring Tool Suite
- Git
- GitHub
- Postman

---

#  Project Structure

## Backend

```text
my-ai-project-backend/
│
├── .gitignore
├── .classpath
├── .project
├── pom.xml
│
├── .settings/
│
└── src/
    └── main/
        │
        ├── java/
        │   └── com/
        │       └── example/
        │           └── rag/
        │               │
        │               ├── ChatbotApplication.java
        │               │
        │               ├── config/
        │               │   └── AppConfig.java
        │               │
        │               ├── controller/
        │               │   ├── ChatController.java
        │               │   ├── DocumentController.java
        │               │   └── HealthController.java
        │               │
        │               └── service/
        │                   ├── GeminiClient.java
        │                   ├── PdfChunker.java
        │                   ├── QdrantClient.java
        │                   └── RagService.java
        │
        └── resources/
            └── application.yml
```

---

#  Prerequisites

Before running the project, make sure the following software is installed.

## Java

Check Java:

```powershell
java -version
```

## Maven

Check Maven:

```powershell
mvn -version
```

## Node.js

Check Node.js:

```powershell
node --version
```

## npm

Check npm:

```powershell
npm --version
```

## Git

Check Git:

```powershell
git --version
```

## Qdrant

Qdrant must be running and accessible according to the project's configuration.

---

#  Getting Gemini API Key

The application uses the Google Gemini API to generate AI responses.

## Step 1

Open Google AI Studio:

https://aistudio.google.com/

## Step 2

Sign in with your Google account.

## Step 3

Create or obtain a Gemini API key.

## Step 4

Copy the API key.

###  Important

Never publish your real Gemini API key on GitHub.

Do NOT put your real key inside:

- README.md
- Java source code
- `application.yml`
- GitHub
- Screenshots
- Public repositories

For documentation, use:

```text
GEMINI_API_KEY=YOUR_GEMINI_API_KEY
```

Never use your actual secret key in the README.

---

# Configuring Gemini API Key

The backend is configured to read the Gemini API key from an environment variable.

The application configuration uses:

```yaml
gemini:
  api-key: ${GEMINI_API_KEY:}
```

This means that the actual API key can be provided through the environment.

The API key does not need to be stored directly in the source code.

---

#  Setting Gemini API Key in PowerShell

Open PowerShell.

Run:

```powershell
$env:GEMINI_API_KEY="YOUR_GEMINI_API_KEY"
```

Replace `YOUR_GEMINI_API_KEY` with your actual Gemini API key.

Example format:

```powershell
$env:GEMINI_API_KEY="your_actual_key_here"
```

### Important

Do not copy your real key into GitHub or share it with anyone.

The environment variable is available to applications started from that PowerShell session.

---

#  Permanent Windows Environment Variable

You can also configure the Gemini API key as a Windows user environment variable.

### Steps

1. Open Windows Search.
2. Search for:

```text
Environment Variables
```

3. Select:

```text
Edit the system environment variables
```

4. Click:

```text
Environment Variables
```

5. Under **User variables**, click **New**.

6. Set:

```text
Variable name:
GEMINI_API_KEY
```

7. Set the value to your Gemini API key.

8. Click **OK**.

9. Close and reopen PowerShell or VS Code.

Do not display the actual API key in screenshots or documentation.

---

#  Running Spring Boot Backend

## Step 1 — Clone the Backend Repository

```bash
git clone https://github.com/dhanya21639/my-ai-project-backend.git
```

## Step 2 — Open the Backend Directory

```powershell
cd my-ai-project-backend
```

## Step 3 — Configure Gemini API Key

In PowerShell:

```powershell
$env:GEMINI_API_KEY="YOUR_GEMINI_API_KEY"
```

## Step 4 — Start Spring Boot

If Maven is installed:

```powershell
mvn spring-boot:run
```

If Maven Wrapper is available:

```powershell
.\mvnw.cmd spring-boot:run
```

You can also run the main Spring Boot class:

```text
ChatbotApplication.java
```

from Eclipse, Spring Tool Suite, IntelliJ IDEA, or VS Code.

---

#  Running React Frontend

Clone the frontend repository:

```bash
git clone https://github.com/dhanya21639/my-ai-project.git
```

Open the frontend directory:

```powershell
cd my-ai-project
```

Install dependencies:

```powershell
npm install
```

Start the React application:

```powershell
npm run dev
```

Vite will display the local development URL in the terminal.

Open the displayed URL in your browser.

---

#  Running the Complete Application

The complete application consists of:

```text
React Frontend
       │
       │ REST API
       ▼
Spring Boot Backend
       │
       ├──────────────► Qdrant
       │
       └──────────────► Google Gemini API
```

### Start the backend first

Make sure:

- Spring Boot is running.
- Gemini API key is configured.
- Qdrant is running.

### Start the frontend

Run:

```powershell
npm install
npm run dev
```

Then open the frontend URL in your browser.

---

#  API Overview

The backend contains the following main controllers.

## ChatController

Responsible for chatbot-related requests.

```text
ChatController
```

It handles communication between the frontend and the RAG chatbot service.

---

## DocumentController

Responsible for document-related operations.

```text
DocumentController
```

It handles document upload and processing requests.

---

## HealthController

Responsible for application health-related operations.

```text
HealthController
```

---

#  Backend Services

## GeminiClient

```text
GeminiClient.java
```

Responsible for communicating with the Google Gemini API.

The Gemini API key is obtained through the environment variable:

```text
GEMINI_API_KEY
```

---

## PdfChunker

```text
PdfChunker.java
```

Responsible for processing PDF content and dividing the content into smaller chunks.

---

## QdrantClient

```text
QdrantClient.java
```

Responsible for communication with the Qdrant vector database.

---

## RagService

```text
RagService.java
```

Coordinates the Retrieval-Augmented Generation process.

The service connects document retrieval with the Gemini AI response generation process.

---

#  Why RAG?

Retrieval-Augmented Generation combines:

```text
Information Retrieval
        +
Large Language Model
```

Instead of asking Gemini to answer using only its general knowledge, the application first retrieves relevant information from the application's document data.

That information is then provided to Gemini as context.

This makes the chatbot useful for document-based question answering.

---

#  Why Qdrant?

Qdrant is used as the vector database for storing and retrieving document information.

The general workflow is:

```text
Document
   ↓
Text Extraction
   ↓
Text Chunks
   ↓
Vector Data
   ↓
Qdrant
   ↓
Similarity Search
   ↓
Relevant Information
```

When the user asks a question, the system retrieves relevant information from Qdrant.

---

#  Security

The project follows basic security practices for API credentials.

The `.gitignore` file contains:

```text
target/
.idea/
.vscode/
*.iml

.env
.env.*

application-local.properties
application-local.yml

*.log
```

This helps prevent sensitive files and generated files from being committed to Git.

The Gemini API key is accessed through:

```text
GEMINI_API_KEY
```

The Spring Boot configuration uses:

```yaml
gemini:
  api-key: ${GEMINI_API_KEY:}
```

---

#  Do NOT Hardcode API Keys

Never do this:

```java
String apiKey = "YOUR_ACTUAL_GEMINI_API_KEY";
```

Never commit this:

```text
GEMINI_API_KEY=YOUR_ACTUAL_SECRET_KEY
```

Instead use:

```text
Environment Variable
        ↓
GEMINI_API_KEY
        ↓
Spring Boot
        ↓
Gemini API
```

---

#  Testing

The backend APIs can be tested using:

- Postman
- Browser for applicable GET endpoints
- React frontend

Before testing the chatbot, make sure:

1. Spring Boot is running.
2. Gemini API key is configured.
3. Qdrant is running.
4. React frontend is running.
5. Frontend is correctly configured to communicate with the backend.

---

#  Important Notes

- Do not commit API keys.
- Do not share Gemini API keys.
- Do not hardcode secrets in Java source code.
- Do not put real API keys inside README files.
- Do not commit `.env` files.
- Do not commit generated `target` files.
- Use environment variables for sensitive credentials.
- Make sure Qdrant is running before using the RAG functionality.

---

#  GitHub Repositories

## Frontend

https://github.com/dhanya21639/my-ai-project

## Backend

https://github.com/dhanya21639/my-ai-project-backend

---

#  Future Enhancements

Possible future improvements include:

- Support for more document formats.
- User authentication and authorization.
- Conversation history.
- Multiple document collections.
- Improved semantic search.
- Streaming AI responses.
- Cloud deployment.
- Advanced monitoring and logging.
- Role-based access control.
- Improved document management.
- Better error handling.
- Multi-user support.

---

#  Author

## Dhanya Shripathi Gouda

MCA | Java | Spring Boot | React | AI / RAG
