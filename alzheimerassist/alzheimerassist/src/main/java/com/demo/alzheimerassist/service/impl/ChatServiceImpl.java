package com.demo.alzheimerassist.service.impl;

import com.demo.alzheimerassist.dto.AIResponse;
import com.demo.alzheimerassist.dto.ChatRequest;
import com.demo.alzheimerassist.dto.ChatResponse;
import com.demo.alzheimerassist.security.SecurityUtils;
import com.demo.alzheimerassist.service.ChatService;
import com.demo.alzheimerassist.service.IntentRouterService;
import com.demo.alzheimerassist.service.OpenAIService;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {

    private final OpenAIService openAIService;
    private final IntentRouterService intentRouterService;
    private final SecurityUtils securityUtils;

    public ChatServiceImpl(
            OpenAIService openAIService,
            IntentRouterService intentRouterService,
            SecurityUtils securityUtils) {

        this.openAIService = openAIService;
        this.intentRouterService = intentRouterService;
        this.securityUtils = securityUtils;
    }

    @Override
    public ChatResponse process(ChatRequest request) {

        Long userId =
                securityUtils.getCurrentUserId();

        AIResponse aiResponse =
                openAIService.analyseMessage(
                        request.getMessage()
                );

        return intentRouterService.route(
                userId,
                request.getMessage(),
                aiResponse
        );
    }
}