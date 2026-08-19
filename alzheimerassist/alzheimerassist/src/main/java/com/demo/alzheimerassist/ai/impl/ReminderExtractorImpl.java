package com.demo.alzheimerassist.ai.impl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.demo.alzheimerassist.ai.ReminderExtractor;
import com.demo.alzheimerassist.dto.AIResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Service
public class ReminderExtractorImpl
        implements ReminderExtractor {


    private final ChatClient chatClient;


    public ReminderExtractorImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
    }


    @Override
    public AIResponse extract(String message, String intent) {

        LocalDateTime now = LocalDateTime.now();

        /*
         * Determine the requested date in Java.
         *
         * This prevents the AI from incorrectly interpreting
         * "tomorrow" as today.
         */
        LocalDate requestedDate = determineRequestedDate(message, now.toLocalDate());


        String prompt = """
You are a reminder information extraction engine for an Alzheimer's Assistant.

The intent has ALREADY been determined by another component.

DO NOT determine the intent again.

Your job is ONLY to extract reminder information.

Return ONLY valid JSON.

Never return Markdown.
Never return explanations.
Never return code fences.

--------------------------------------------------
CURRENT DATE AND TIME
--------------------------------------------------

%s

--------------------------------------------------
DETECTED INTENT
--------------------------------------------------

%s

You MUST return exactly this intent.

Never change the intent.

Never return GET_REMINDERS.

--------------------------------------------------
REQUESTED REMINDER DATE
--------------------------------------------------

The application has already determined the requested calendar date.

Requested date:

%s

IMPORTANT:

If the user asks for:

"today"

use the requested date above.

If the user asks for:

"tomorrow"

use the requested date above.

DO NOT calculate the date yourself.

DO NOT change the requested date.

--------------------------------------------------
VALID INTENTS
--------------------------------------------------

STORE_REMINDER
GET_TODAYS_REMINDERS
MARK_REMINDER_COMPLETE

--------------------------------------------------
GET_TODAYS_REMINDERS
--------------------------------------------------

This intent is used when the user asks to see reminders
for a particular calendar date.

Examples:

What are my reminders today?

What are my reminders tomorrow?

What are tomorrow's reminders?

What reminders do I have tomorrow?

Do I have any reminders tomorrow?

Show me my reminders for tomorrow.

For this intent return:

"reminderText": null

"repeatType": null

"reminderDateTime":
the requested date at 00:00:00

IMPORTANT:

Use EXACTLY the requested date supplied by the application.

--------------------------------------------------
STORE_REMINDER
--------------------------------------------------

Use this when the user wants to create a reminder.

Extract:

reminderText
reminderDateTime
repeatType

Examples:

Remind me to take medicine at 8 PM.

Remind me tomorrow to call my daughter.

Remind me every Sunday to call my daughter.

--------------------------------------------------
MARK_REMINDER_COMPLETE
--------------------------------------------------

Examples:

I have taken my medicine.

I took my tablet.

I have completed my medicine.

Medicine completed.

--------------------------------------------------
DATE RULES
--------------------------------------------------

The application has already calculated the requested date.

Do not calculate dates yourself.

The date must be returned in:

yyyy-MM-ddTHH:mm:ss

format.

For GET_TODAYS_REMINDERS, the time must be:

00:00:00

--------------------------------------------------
FIELD RULES
--------------------------------------------------

Always return these four fields:

intent
reminderText
reminderDateTime
repeatType

Never return empty strings.

Use null when a field is unknown or not applicable.

--------------------------------------------------
USER MESSAGE
--------------------------------------------------

%s
""".formatted(
                now,
                intent,
                requestedDate.atStartOfDay(),
                message
        );


        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .entity(AIResponse.class);
    }


    /**
     * Determines the calendar date requested by the user.
     *
     * This is deliberately kept simple so that existing
     * reminder functionality is not affected.
     */
    private LocalDate determineRequestedDate(
            String message,
            LocalDate currentDate) {

        String text = message.toLowerCase();

        if (text.contains("tomorrow")) {
            return currentDate.plusDays(1);
        }

        if (text.contains("yesterday")) {
            return currentDate.minusDays(1);
        }

        // Default to today.
        return currentDate;
    }

}