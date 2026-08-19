package com.example.rag.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.*;

@Component
public class PdfChunker {
	@Value("${rag.chunk-size:1200}")
	int chunkSize;
	@Value("${rag.chunk-overlap:200}")
	int overlap;

	public List<Chunk> chunk(InputStream in) throws Exception {
		byte[] bytes = in.readAllBytes();
		List<Chunk> out = new ArrayList<>();
		try (PDDocument doc = Loader.loadPDF(bytes)) {
			PDFTextStripper stripper = new PDFTextStripper();
			int id = 0;
			for (int p = 1; p <= doc.getNumberOfPages(); p++) {
				stripper.setStartPage(p);
				stripper.setEndPage(p);
				String text = clean(stripper.getText(doc));
				if (text.isBlank())
					continue;
				int start = 0, size = Math.max(200, chunkSize), ov = Math.min(Math.max(0, overlap), size - 1);
				while (start < text.length()) {
					int end = Math.min(text.length(), start + size);
					String part = text.substring(start, end).trim();
					if (!part.isBlank())
						out.add(new Chunk("chunk-" + (id++), p, part));
					if (end >= text.length())
						break;
					start = end - ov;
				}
			}
		}
		return out;
	}

	private String clean(String s) {
		return s.replace("\r", "\n").replaceAll("[ \\t]+", " ").replaceAll("\\n{3,}", "\\n\\n").trim();
	}

	public record Chunk(String chunkId, int page, String text) {
	}
}
