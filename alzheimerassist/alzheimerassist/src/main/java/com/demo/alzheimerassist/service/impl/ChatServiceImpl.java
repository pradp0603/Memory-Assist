package com.demo.alzheimerassist.service.impl;

import com.demo.alzheimerassist.service.IntentRouterService;
import com.demo.alzheimerassist.service.MemoryService;
import com.demo.alzheimerassist.service.OpenAIService;
import org.springframework.stereotype.Service;

import com.demo.alzheimerassist.dto.ChatRequest;
import com.demo.alzheimerassist.dto.ChatResponse;
import com.demo.alzheimerassist.service.ChatService;
import com.demo.alzheimerassist.dto.AIResponse;
import com.demo.alzheimerassist.dto.MemoryResponse;
import com.demo.alzheimerassist.entity.IntentType;

@Service
public class ChatServiceImpl implements ChatService {

    private final OpenAIService openAIService;

    private final MemoryService memoryService;

    private final IntentRouterService intentRouterService;

    public ChatServiceImpl(OpenAIService openAIService, MemoryService memoryService, IntentRouterService intentRouterService) {
        this.openAIService = openAIService;
        this.memoryService = memoryService;
        this.intentRouterService = intentRouterService;
    }


    @Override
    public ChatResponse process(ChatRequest request){

        AIResponse aiResponse =

                openAIService.analyseMessage(

                        request.getMessage());

        return intentRouterService.route(

                request,

                aiResponse);

    }

}