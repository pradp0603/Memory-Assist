package com.demo.alzheimerassist.controller;

import org.springframework.web.bind.annotation.*;

import com.demo.alzheimerassist.dto.ChatRequest;
import com.demo.alzheimerassist.dto.ChatResponse;
import com.demo.alzheimerassist.service.ChatService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService service;

    public ChatController(ChatService service) {
        this.service = service;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {

        return service.process(request);

    }

}