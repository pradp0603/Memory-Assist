package com.demo.alzheimerassist.service;

import com.demo.alzheimerassist.dto.AIResponse;
import com.demo.alzheimerassist.dto.ChatRequest;
import com.demo.alzheimerassist.dto.ChatResponse;

public interface IntentRouterService {

    ChatResponse route(ChatRequest request,
                       AIResponse aiResponse);

}