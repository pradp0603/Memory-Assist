package com.demo.alzheimerassist.service;

import com.demo.alzheimerassist.dto.AIResponse;

public interface OpenAIService {

    AIResponse analyseMessage(String message);

}