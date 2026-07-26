package com.demo.alzheimerassist.service;

import com.demo.alzheimerassist.dto.ChatRequest;
import com.demo.alzheimerassist.dto.ChatResponse;

public interface ChatService {

    ChatResponse process(ChatRequest request);

}