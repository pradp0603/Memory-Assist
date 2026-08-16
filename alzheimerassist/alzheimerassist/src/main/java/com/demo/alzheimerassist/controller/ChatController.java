package com.demo.alzheimerassist.controller;

import com.demo.alzheimerassist.dto.AIResponse;
import com.demo.alzheimerassist.service.OpenAIService;
import org.springframework.web.bind.annotation.*;

import com.demo.alzheimerassist.dto.ChatRequest;
import com.demo.alzheimerassist.dto.ChatResponse;
import com.demo.alzheimerassist.service.ChatService;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    private final OpenAIService openAIService;


    public ChatController(ChatService chatService, OpenAIService openAIService) {
        this.chatService = chatService;
        this.openAIService = openAIService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {

        return chatService.process(request);

    }

    @PostMapping("/chat/ai-test")
    public AIResponse aiTest(@RequestBody ChatRequest request) {

        return openAIService.analyseMessage(request.getMessage());

    }

}