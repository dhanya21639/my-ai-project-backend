package com.example.rag.service;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.*;
import java.net.http.*;
import java.time.Duration;
import java.util.*;

@Service
public class QdrantClient {
	private final ObjectMapper mapper = new ObjectMapper();
	private final HttpClient http = HttpClient.newHttpClient();
	@Value("${rag.qdrant-url:http://localhost:6333}")
	String url;
	@Value("${rag.collection:pdf_chunks}")
	String collection;
	@Value("${gemini.embedding-dimension:1536}")
	int size;

	public void ensureCollection() {
		try {
			var r = req("GET", "/collections/" + collection, null);
			if (r.statusCode() == 200)
				return;
			ObjectNode body = mapper.createObjectNode();
			body.putObject("vectors").put("size", size).put("distance", "Cosine");
			r = req("PUT", "/collections/" + collection, body);
			if (r.statusCode() < 200 || r.statusCode() >= 300)
				throw new IllegalStateException(r.body());
		} catch (Exception e) {
			throw new RuntimeException("Qdrant initialization failed: " + e.getMessage(), e);
		}
	}

	public void upsert(String docId, String file, List<PdfChunker.Chunk> chunks, List<List<Double>> vectors) {
		try {
			ArrayNode points = mapper.createArrayNode();
			for (int i = 0; i < chunks.size(); i++) {
				var c = chunks.get(i);
				ObjectNode p = mapper.createObjectNode();
				p.put("id", UUID.randomUUID().toString());
				ArrayNode v = mapper.createArrayNode();
				vectors.get(i).forEach(v::add);
				p.set("vector", v);
				ObjectNode pl = p.putObject("payload");
				pl.put("documentId", docId);
				pl.put("fileName", file);
				pl.put("chunkId", c.chunkId());
				pl.put("page", c.page());
				pl.put("text", c.text());
				points.add(p);
			}
			ObjectNode body = mapper.createObjectNode();
			body.set("points", points);
			var r = req("PUT", "/collections/" + collection + "/points?wait=true", body);
			if (r.statusCode() < 200 || r.statusCode() >= 300)
				throw new IllegalStateException(r.body());
		} catch (Exception e) {
			throw new RuntimeException("Qdrant upsert failed: " + e.getMessage(), e);
		}
	}

	public List<SearchResult> search(String docId, List<Double> vector, int limit, double threshold) {
		try {
			ObjectNode body = mapper.createObjectNode();
			ArrayNode v = mapper.createArrayNode();
			vector.forEach(v::add);
			body.set("query", v);
			body.put("limit", limit);
			body.put("with_payload", true);
			body.put("score_threshold", threshold);
			ObjectNode condition = mapper.createObjectNode();
			condition.put("key", "documentId");
			condition.putObject("match").put("value", docId);
			body.putObject("filter").putArray("must").add(condition);
			var r = req("POST", "/collections/" + collection + "/points/query", body);
			if (r.statusCode() < 200 || r.statusCode() >= 300)
				throw new IllegalStateException(r.body());
			List<SearchResult> out = new ArrayList<>();
			for (JsonNode x : mapper.readTree(r.body()).path("result").path("points")) {
				var p = x.path("payload");
				out.add(new SearchResult(x.path("score").asDouble(), p.path("documentId").asText(),
						p.path("fileName").asText(), p.path("chunkId").asText(), p.path("page").asInt(),
						p.path("text").asText()));
			}
			return out;
		} catch (Exception e) {
			throw new RuntimeException("Qdrant search failed: " + e.getMessage(), e);
		}
	}

	public record SearchResult(double score, String documentId, String fileName, String chunkId, int page,
			String text) {
	}

	private HttpResponse<String> req(String method, String path, JsonNode body) throws Exception {
		HttpRequest.Builder b = HttpRequest.newBuilder().uri(URI.create(url.replaceAll("/+$", "") + path))
				.timeout(Duration.ofSeconds(30)).header("Content-Type", "application/json");
		String s = body == null ? "" : body.toString();
		if (method.equals("GET"))
			b.GET();
		else if (method.equals("PUT"))
			b.PUT(HttpRequest.BodyPublishers.ofString(s));
		else
			b.POST(HttpRequest.BodyPublishers.ofString(s));
		return http.send(b.build(), HttpResponse.BodyHandlers.ofString());
	}
}
