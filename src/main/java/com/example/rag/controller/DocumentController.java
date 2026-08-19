package com.example.rag.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.rag.service.RagService;

//import com.example.rag.RagService;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
	private final RagService service;

	public DocumentController(RagService s) {
		service = s;
	}

	@PostMapping("/upload")
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
		try {
			return ResponseEntity.ok(service.upload(file));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(new ErrorResponse(e.getMessage() == null ? "Upload failed." : e.getMessage()));
		}
	}

	record ErrorResponse(String error) {
	}
}
