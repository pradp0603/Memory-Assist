package com.demo.alzheimerassist.service.impl;

import com.demo.alzheimerassist.ai.ContactExtractor;
import com.demo.alzheimerassist.ai.IntentDetector;
import com.demo.alzheimerassist.ai.MemoryExtractor;
import com.demo.alzheimerassist.ai.ReminderExtractor;
import com.demo.alzheimerassist.entity.IntentType;
import org.springframework.stereotype.Service;

import com.demo.alzheimerassist.dto.AIResponse;
import com.demo.alzheimerassist.service.OpenAIService;

@Service
public class OpenAIServiceImpl implements OpenAIService {

    private final MemoryExtractor memoryExtractor;
    private final IntentDetector intentDetector;
    private final ContactExtractor contactExtractor;
    private final ReminderExtractor reminderExtractor;

    public OpenAIServiceImpl(MemoryExtractor memoryExtractor, IntentDetector intentDetector,
                             ContactExtractor contactExtractor, ReminderExtractor reminderExtractor) {
        this.memoryExtractor = memoryExtractor;
        this.intentDetector = intentDetector;
        this.contactExtractor = contactExtractor;
        this.reminderExtractor = reminderExtractor;
    }


    @Override
    public AIResponse analyseMessage(String message) {

        String detectedIntent = intentDetector.detectIntent(message);

        System.out.println("Detected intent: " + detectedIntent);

        AIResponse aiResponse;

        switch (detectedIntent) {

            case "STORE_MEMORY":
            case "RETRIEVE_MEMORY":
            case "DELETE_MEMORY":

                aiResponse = memoryExtractor.extract(message, detectedIntent);
                break;

            case "STORE_CONTACT":
            case "GET_CONTACT":
            case "DELETE_CONTACT":
            case "UPDATE_CONTACT":

                aiResponse = contactExtractor.extract(message, detectedIntent);
                break;

            case "STORE_REMINDER":
            case "GET_TODAYS_REMINDERS":
            case "MARK_REMINDER_COMPLETE":

                aiResponse = reminderExtractor.extract(message, detectedIntent);
                break;

            default:

                aiResponse = new AIResponse();
                break;
        }

        /*
         * The intent has already been determined by IntentDetector.
         *
         * Do not allow the extractor to overwrite it with null
         * or a different intent.
         */
        aiResponse.setIntent(
                IntentType.valueOf(detectedIntent)
        );

        return aiResponse;
    }

}