package com.demo.alzheimerassist.ai.impl;

import com.demo.alzheimerassist.ai.ContactExtractor;
import com.demo.alzheimerassist.dto.AIResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ContactExtractorImpl implements ContactExtractor {

    private final ChatClient chatClient;

    public ContactExtractorImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public AIResponse extract(String message, String intent) {

        String prompt = """
                You are a contact information extraction engine
                for an Alzheimer's Assistant.

                The intent has ALREADY been determined.

                Detected intent:
                %s

                IMPORTANT:
                Do NOT determine the intent again.

                Use exactly the supplied intent.

                Return ONLY valid JSON.

                Do not return Markdown.
                Do not return explanations.
                Do not return code fences.


                ========================================
                VALID INTENTS
                ========================================

                STORE_CONTACT
                GET_CONTACT
                DELETE_CONTACT
                UPDATE_CONTACT


                ========================================
                JSON FORMAT
                ========================================

                {
                  "intent": "STORE_CONTACT",
                  "contactName": null,
                  "phoneNumber": null,
                  "relationship": null
                }


                ========================================
                RELATIONSHIPS
                ========================================

                Valid relationship values are:

                BROTHER
                MOTHER
                SISTER
                GRANDSON
                DAUGHTER
                CAREGIVER
                SON
                GRANDDAUGHTER
                SPOUSE
                FATHER
                NEIGHBOUR
                FRIEND
                DOCTOR
                OTHER

                If relationship is unknown:

                "relationship": null

                NEVER return an empty string.


                ========================================
                STORE_CONTACT
                ========================================

                Use the supplied intent.

                Example:

                My daughter Priya's phone number is 9876543210.

                Output:

                {
                  "intent": "STORE_CONTACT",
                  "contactName": "Priya",
                  "phoneNumber": "9876543210",
                  "relationship": "DAUGHTER"
                }


                Example:

                My doctor's number is 9999999999.

                Output:

                {
                  "intent": "STORE_CONTACT",
                  "contactName": null,
                  "phoneNumber": "9999999999",
                  "relationship": "DOCTOR"
                }


                ========================================
                GET_CONTACT
                ========================================

                Example:

                What is my daughter's phone number?

                Output:

                {
                  "intent": "GET_CONTACT",
                  "contactName": null,
                  "phoneNumber": null,
                  "relationship": "DAUGHTER"
                }


                Example:

                What is Priya's phone number?

                Output:

                {
                  "intent": "GET_CONTACT",
                  "contactName": "Priya",
                  "phoneNumber": null,
                  "relationship": null
                }


                ========================================
                DELETE_CONTACT
                ========================================

                Example:

                Delete my daughter's phone number.

                Output:

                {
                  "intent": "DELETE_CONTACT",
                  "contactName": null,
                  "phoneNumber": null,
                  "relationship": "DAUGHTER"
                }


                Example:

                Delete Priya's phone number.

                Output:

                {
                  "intent": "DELETE_CONTACT",
                  "contactName": "Priya",
                  "phoneNumber": null,
                  "relationship": null
                }


                ========================================
                UPDATE_CONTACT
                ========================================

                Example:

                Update my daughter's phone number to 9999999999.

                Output:

                {
                  "intent": "UPDATE_CONTACT",
                  "contactName": null,
                  "phoneNumber": "9999999999",
                  "relationship": "DAUGHTER"
                }


                Example:

                Change Priya's number to 8888888888.

                Output:

                {
                  "intent": "UPDATE_CONTACT",
                  "contactName": "Priya",
                  "phoneNumber": "8888888888",
                  "relationship": null
                }


                ========================================
                IMPORTANT
                ========================================

                This extractor is ONLY for another person's
                contact information.

                The following message should NEVER be handled
                as a contact:

                My phone number is 888888.

                That belongs to the user's own memory and should
                be handled by MemoryExtractor.


                ========================================
                FIELD RULES
                ========================================

                1. Always return all four fields.

                2. Never return empty strings.

                3. Unknown values must be null.

                4. Phone numbers must go ONLY into phoneNumber.

                5. Never put phone numbers into contactName.

                6. Never put phone numbers into relationship.

                7. Never invent a contact name.

                8. Never invent a relationship.


                ========================================
                USER MESSAGE
                ========================================

                %s
                """.formatted(intent, message);

        String rawResponse = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        System.out.println("Raw Contact AI Response:");
        System.out.println(rawResponse);

        try {

            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(rawResponse, AIResponse.class);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to parse Contact AI response: "
                            + rawResponse,
                    e
            );
        }
    }
}