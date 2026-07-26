package com.demo.alzheimerassist.service.impl;

import com.demo.alzheimerassist.dto.AIResponse;
import com.demo.alzheimerassist.dto.ChatRequest;
import com.demo.alzheimerassist.dto.ChatResponse;
import com.demo.alzheimerassist.dto.MemoryResponse;
import com.demo.alzheimerassist.service.*;
import org.springframework.stereotype.Service;

@Service
public class IntentRouterServiceImpl implements IntentRouterService {

    private final MemoryService memoryService;

    private final ContactService contactService;

    private final ReminderService reminderService;

    private final ResponseFormatterService formatter;

    public IntentRouterServiceImpl(MemoryService memoryService, ContactService contactService, ReminderService reminderService, ResponseFormatterService formatter){

        this.memoryService=memoryService;

        this.contactService=contactService;

        this.reminderService=reminderService;

        this.formatter=formatter;

    }

    @Override
    public ChatResponse route(ChatRequest request, AIResponse ai){

        switch (ai.getIntent()) {

            case RETRIEVE_MEMORY -> {

                MemoryResponse response = memoryService.getMemory(request.getUserId(), ai.getMemoryType());

                return formatter.formatMemory(response);

            }

            default -> {

                return formatter.formatError("Sorry, I couldn't understand your request.");

            }

        }

    }
}