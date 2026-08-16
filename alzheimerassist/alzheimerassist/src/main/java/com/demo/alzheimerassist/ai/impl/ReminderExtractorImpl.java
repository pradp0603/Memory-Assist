package com.demo.alzheimerassist.ai.impl;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.demo.alzheimerassist.ai.ReminderExtractor;
import com.demo.alzheimerassist.dto.AIResponse;

import java.time.LocalDateTime;


@Service
public class ReminderExtractorImpl
        implements ReminderExtractor {


    private final ChatClient chatClient;


    public ReminderExtractorImpl(ChatClient chatClient){
        this.chatClient = chatClient;
    }



    @Override
    public AIResponse extract(String message, String intent) {

        LocalDateTime now = LocalDateTime.now();


        String prompt = """
You are a reminder information extraction engine for an Alzheimer's Assistant.

The intent has ALREADY been determined by another component.

DO NOT determine the intent.

Your job is ONLY to extract reminder information such as:

- reminder text
- reminder date/time
- repeat type

Return ONLY valid JSON.

Never return Markdown.
Never return explanations.
Never return code fences.

The current date and time is:

%s

--------------------------------------------------
IMPORTANT
--------------------------------------------------

The previously detected intent is:

%s

You MUST use this exact intent in the JSON response.

Never change it.

Never return GET_REMINDERS.

--------------------------------------------------
VALID INTENTS
--------------------------------------------------

STORE_REMINDER
GET_TODAYS_REMINDERS
MARK_REMINDER_COMPLETE

--------------------------------------------------
DATE RULES
--------------------------------------------------

"today" means the current calendar date.

"tomorrow" means the next calendar date.

"yesterday" means the previous calendar date.

For example, if the current date is:

2026-08-13

then:

today = 2026-08-13
tomorrow = 2026-08-14
yesterday = 2026-08-12

--------------------------------------------------
GET_TODAYS_REMINDERS
--------------------------------------------------

When the user asks for reminders for a particular day,
return:

"intent": "GET_TODAYS_REMINDERS"

This includes:

What are my reminders today?

What are my reminders tomorrow?

What are tomorrow's reminders?

What reminders do I have tomorrow?

Do I have any reminders tomorrow?

Show me my reminders for tomorrow.

For GET_TODAYS_REMINDERS:

reminderText = null

repeatType = null

reminderDateTime = the requested date at 00:00:00

--------------------------------------------------
STORE_REMINDER
--------------------------------------------------

Extract:

reminderText
reminderDateTime
repeatType

Examples:

Remind me to take medicine at 8 PM.

Remind me every Sunday to call my daughter.

Remind me tomorrow to call my daughter.

--------------------------------------------------
MARK_REMINDER_COMPLETE
--------------------------------------------------

Examples:

I have taken my medicine.

I took my tablet.

I have completed my medicine.

Medicine completed.

--------------------------------------------------
FIELD RULES
--------------------------------------------------

Always return:

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
""".formatted(now, intent, message);



        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .entity(AIResponse.class);
    }

}