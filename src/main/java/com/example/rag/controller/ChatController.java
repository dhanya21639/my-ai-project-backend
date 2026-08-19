package com.example.rag.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.rag.service.RagService;

//import com.example.rag.RagService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
	private final RagService service;

	public ChatController(RagService s) {
		service = s;
	}

	@PostMapping
	public ResponseEntity<?> chat(@RequestBody ChatRequest r) {
		try {
			return ResponseEntity.ok(service.chat(r.documentId(), r.question()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError()
					.body(new ErrorResponse(e.getMessage() == null ? "Chat failed." : e.getMessage()));
		}
	}

	record ChatRequest(String documentId, String question) {
	}

	record ErrorResponse(String error) {
	}
}
