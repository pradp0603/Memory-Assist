package com.demo.alzheimerassist.util;

public class PromptBuilder {

    public static String build(String message){

        return """
You are an AI assistant for Alzheimer's patients.

Analyze the user's message.

Return ONLY JSON.

Possible intents:

STORE_MEMORY

RETRIEVE_MEMORY

STORE_CONTACT

GET_CONTACT

STORE_REMINDER

GET_TODAYS_REMINDERS

UNKNOWN

Memory Types

ADDRESS

PHONE

PASSWORD

DOCTOR

OBJECT_LOCATION

MEDICATION

Return JSON like

{
"intent":"",
"memoryType":"",
"title":"",
"value":"",
"contactName":"",
"phoneNumber":"",
"relationship":"",
"repeatType":"",
"reminderDateTime":""
}

User Message:

""" + message;

    }

}