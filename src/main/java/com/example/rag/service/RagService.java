package com.example.rag.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Service
public class RagService {
	private final PdfChunker chunker;
	private final GeminiClient gemini;
	private final QdrantClient qdrant;
	@Value("${rag.top-k:5}")
	int topK;
	@Value("${rag.score-threshold:0.20}")
	double threshold;
	@Value("${chat.no-document-message}")
	String noDoc;
	@Value("${chat.no-answer-message}")
	String noAnswer;

	public RagService(PdfChunker c, GeminiClient g, QdrantClient q) {
		chunker = c;
		gemini = g;
		qdrant = q;
		qdrant.ensureCollection();
	}

	public UploadResponse upload(MultipartFile file) throws Exception {
		if (file == null || file.isEmpty())
			throw new IllegalArgumentException("Please select a PDF file.");
		String name = file.getOriginalFilename() == null ? "document.pdf" : file.getOriginalFilename();
		if (!name.toLowerCase(Locale.ROOT).endsWith(".pdf"))
			throw new IllegalArgumentException("Only PDF files are supported.");
		List<PdfChunker.Chunk> chunks = chunker.chunk(file.getInputStream());
		if (chunks.isEmpty())
			throw new IllegalArgumentException("No readable text was found in the PDF. Scanned PDFs need OCR.");
		String id = UUID.randomUUID().toString();
		List<List<Double>> vectors = new ArrayList<>();
		for (var c : chunks)
			vectors.add(gemini.embedDocument(c.text()));
		qdrant.upsert(id, name, chunks, vectors);
		return new UploadResponse(id, name, chunks.size(), "Document uploaded and indexed successfully.");
	}

	public ChatResponse chat(String doc, String q) {
		if (q == null || q.isBlank())
			return new ChatResponse("Please enter a question.", List.of());
		q = q.trim();
		if (smallTalk(q))
			return new ChatResponse(gemini.smallTalk(q), List.of());
		if (doc == null || doc.isBlank())
			return new ChatResponse(noDoc, List.of());
		List<QdrantClient.SearchResult> results = qdrant.search(doc, gemini.embedQuery(q), topK, threshold);
		if (results.isEmpty())
			return new ChatResponse(noAnswer, List.of());
		String answer = gemini.answer(q, results);
		if (answer.isBlank() || answer.contains("DOCUMENT_ANSWER_NOT_FOUND"))
			return new ChatResponse(noAnswer, sources(results));
		return new ChatResponse(answer, sources(results));
	}

	private boolean smallTalk(String s) {
		String q = s.toLowerCase(Locale.ROOT).replaceAll("[^a-z ]", "").trim();
		return q.matches("^(hi|hello|hey|hai|good morning|good afternoon|good evening)$")
				|| q.matches("^(how are you|how r you|how are u)$") || q.matches("^(thanks|thank you|thx)$")
				|| q.matches("^(bye|goodbye)$");
	}

	private List<Source> sources(List<QdrantClient.SearchResult> r) {
		return r.stream().map(x -> new Source(x.fileName(), x.page(), x.score())).toList();
	}

	public record UploadResponse(String documentId, String fileName, int chunks, String message) {
	}

	public record ChatResponse(String answer, List<Source> sources) {
	}

	public record Source(String fileName, int page, double score) {
	}
}
