package com.example.rag.service;

//import com.example.rag.QdrantClient;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.*;
import java.net.http.*;
import java.time.Duration;
import java.util.*;

@Service
public class GeminiClient {
	private final ObjectMapper mapper = new ObjectMapper();
	private final HttpClient http = HttpClient.newHttpClient();
	@Value("${gemini.api-key:}")
	String apiKey;
	@Value("${gemini.base-url:https://generativelanguage.googleapis.com/v1beta}")
	String baseUrl;
	@Value("${gemini.embedding-model:gemini-embedding-001}")
	String embeddingModel;
	@Value("${gemini.chat-model:gemini-2.5-flash-lite}")
	String chatModel;
	@Value("${gemini.embedding-dimension:1536}")
	int dimension;
	@Value("${gemini.timeout-seconds:90}")
	int timeout;

	public List<Double> embedDocument(String text) {
		return embed(text, "RETRIEVAL_DOCUMENT");
	}

	public List<Double> embedQuery(String text) {
		return embed(text, "RETRIEVAL_QUERY");
	}

	private List<Double> embed(String text, String task) {
		key();
		try {
			ObjectNode body = mapper.createObjectNode();
			body.put("model", "models/" + embeddingModel);
			ObjectNode content = body.putObject("content");
			content.putArray("parts").addObject().put("text", text);
			ObjectNode cfg = body.putObject("embedContentConfig");
			cfg.put("taskType", task);
			cfg.put("outputDimensionality", dimension);
			HttpResponse<String> r = post(baseUrl + "/models/" + embeddingModel + ":embedContent", body);
			check(r, "embedding");
			JsonNode vals = mapper.readTree(r.body()).path("embedding").path("values");
			if (!vals.isArray() || vals.isEmpty())
				throw new IllegalStateException("Gemini returned no embedding: " + r.body());
			List<Double> list = new ArrayList<>();
			vals.forEach(v -> list.add(v.asDouble()));
			return list;
		} catch (Exception e) {
			throw new RuntimeException("Gemini embedding error: " + e.getMessage(), e);
		}
	}

	public String answer(String q, List<QdrantClient.SearchResult> results) {
		key();
		try {
			StringBuilder ctx = new StringBuilder();
			for (int i = 0; i < results.size(); i++) {
				var x = results.get(i);
				ctx.append("SOURCE ").append(i + 1).append(" | page ").append(x.page()).append("\n").append(x.text())
						.append("\n\n");
			}
			String prompt = "Answer the user's question using ONLY the document context below. Do not invent facts. If the answer is not in the context, reply exactly DOCUMENT_ANSWER_NOT_FOUND.\n\nDOCUMENT CONTEXT:\n"
					+ ctx + "\nUSER QUESTION:\n" + q;
			ObjectNode body = mapper.createObjectNode();
			body.putObject("systemInstruction").putArray("parts").addObject().put("text",
					"You are a precise document assistant. Use only supplied context.");
			ObjectNode user = body.putArray("contents").addObject();
			user.put("role", "user");
			user.putArray("parts").addObject().put("text", prompt);
			body.putObject("generationConfig").put("temperature", 0.1).put("maxOutputTokens", 1000);
			HttpResponse<String> r = post(baseUrl + "/models/" + chatModel + ":generateContent", body);
			check(r, "chat");
			return mapper.readTree(r.body()).path("candidates").get(0).path("content").path("parts").get(0).path("text")
					.asText("").trim();
		} catch (Exception e) {
			throw new RuntimeException("Gemini chat error: " + e.getMessage(), e);
		}
	}

	public String smallTalk(String q) {
		key();
		try {
			ObjectNode body = mapper.createObjectNode();
			body.putArray("contents").addObject().put("role", "user").putArray("parts").addObject().put("text",
					"Reply briefly and naturally to this simple greeting: " + q);
			body.putObject("generationConfig").put("temperature", 0.4).put("maxOutputTokens", 100);
			HttpResponse<String> r = post(baseUrl + "/models/" + chatModel + ":generateContent", body);
			check(r, "small-talk");
			return mapper.readTree(r.body()).path("candidates").get(0).path("content").path("parts").get(0).path("text")
					.asText("").trim();
		} catch (Exception e) {
			throw new RuntimeException("Gemini small-talk error: " + e.getMessage(), e);
		}
	}

	private HttpResponse<String> post(String url, JsonNode body) throws Exception {
		return http.send(
				HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(timeout))
						.header("Content-Type", "application/json").header("x-goog-api-key", apiKey)
						.POST(HttpRequest.BodyPublishers.ofString(body.toString())).build(),
				HttpResponse.BodyHandlers.ofString());
	}

	private void check(HttpResponse<String> r, String op) {
		if (r.statusCode() < 200 || r.statusCode() >= 300)
			throw new IllegalStateException("Gemini " + op + " failed (" + r.statusCode() + "): " + r.body());
	}

	private void key() {
		if (apiKey == null || apiKey.isBlank())
			throw new IllegalStateException("GEMINI_API_KEY is not configured. Set it in the backend environment.");
	}
}
