package com.demo.alzheimerassist.service;

import com.demo.alzheimerassist.dto.AIResponse;
import com.demo.alzheimerassist.dto.ChatResponse;

public interface IntentRouterService {

    ChatResponse route(
            Long userId,
            String message,
            AIResponse aiResponse
    );
}