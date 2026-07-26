package com.demo.alzheimerassist.service.impl;

import org.springframework.stereotype.Service;

import com.demo.alzheimerassist.dto.AIResponse;
import com.demo.alzheimerassist.entity.IntentType;
import com.demo.alzheimerassist.entity.MemoryType;
import com.demo.alzheimerassist.service.OpenAIService;

@Service
public class OpenAIServiceImpl implements OpenAIService {

    @Override
    public AIResponse analyseMessage(String message) {

        AIResponse response = new AIResponse();

        if(message.toLowerCase().contains("address")){

            response.setIntent(IntentType.RETRIEVE_MEMORY);

            response.setMemoryType(MemoryType.ADDRESS);

        }

        else{

            response.setIntent(IntentType.UNKNOWN);

        }

        return response;

    }

}