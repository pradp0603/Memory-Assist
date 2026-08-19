package com.demo.alzheimerassist.ai.impl;

import com.demo.alzheimerassist.ai.MemoryExtractor;
import com.demo.alzheimerassist.dto.AIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class MemoryExtractorImpl implements MemoryExtractor {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public MemoryExtractorImpl(
            ChatClient chatClient,
            ObjectMapper objectMapper) {

        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public AIResponse extract(String message, String intent) {

        String prompt = """
                You are an information extraction engine
                for an Alzheimer's Assistant.

                The intent has ALREADY been determined.

                Detected intent:
                %s

                IMPORTANT:
                Do NOT determine the intent again.

                Use exactly the supplied intent.

                Return ONLY one valid JSON object.

                Do not return Markdown.
                Do not return explanations.
                Do not return code fences.

                ========================================
                JSON FORMAT
                ========================================

                {
                  "intent": "STORE_MEMORY",
                  "memoryType": "PHONE",
                  "memoryTypeName": null,
                  "title": "Phone Number",
                  "value": "888888"
                }

                ========================================
                VALID INTENTS
                ========================================

                STORE_MEMORY
                RETRIEVE_MEMORY
                DELETE_MEMORY

                ========================================
                VALID MEMORY TYPES
                ========================================

                ADDRESS
                PHONE
                EMAIL
                PASSWORD
                OBJECT_LOCATION
                OTHER

                Never create a new memoryType.

                ========================================
                MEMORY TYPE RULES
                ========================================

                ADDRESS

                Use ADDRESS for the user's address.

                Example:

                My address is 50 High Street, Rugby.

                Output:

                {
                  "intent": "STORE_MEMORY",
                  "memoryType": "ADDRESS",
                  "memoryTypeName": null,
                  "title": "Home Address",
                  "value": "50 High Street, Rugby"
                }

                ----------------------------------------

                PHONE

                Use PHONE when the user is storing
                THEIR OWN phone number.

                Example:

                My phone number is 888888.

                Output:

                {
                  "intent": "STORE_MEMORY",
                  "memoryType": "PHONE",
                  "memoryTypeName": null,
                  "title": "Phone Number",
                  "value": "888888"
                }

                ----------------------------------------

                EMAIL

                Example:

                My email is abc@gmail.com.

                Output:

                {
                  "intent": "STORE_MEMORY",
                  "memoryType": "EMAIL",
                  "memoryTypeName": null,
                  "title": "Email Address",
                  "value": "abc@gmail.com"
                }

                ----------------------------------------

                PASSWORD

                Example:

                My Gmail password is Abc@123.

                Output:

                {
                  "intent": "STORE_MEMORY",
                  "memoryType": "PASSWORD",
                  "memoryTypeName": null,
                  "title": "Gmail Password",
                  "value": "Abc@123"
                }

                ----------------------------------------
                
                BANK ACCOUNT INFORMATION
                
                If the user provides a bank account number and/or sort code,
                use:
                
                "memoryType": "OTHER"
                "memoryTypeName": "BANK_ACCOUNT"
                
                The title must be:
                
                "Bank Account"
                
                If both account number and sort code are provided,
                store both values together in the value field.
                
                Example:
                
                User:
                My bank account number is 123456 and sort code is 55-55-66.
                
                Output:
                {
                  "intent": "STORE_MEMORY",
                  "memoryType": "OTHER",
                  "memoryTypeName": "BANK_ACCOUNT",
                  "title": "Bank Account",
                  "value": "Account Number: 123456; Sort Code: 55-55-66"
                }
                
                If only the account number is provided:
                
                User:
                My bank account number is 123456.
                
                Output:
                {
                  "intent": "STORE_MEMORY",
                  "memoryType": "OTHER",
                  "memoryTypeName": "BANK_ACCOUNT",
                  "title": "Bank Account",
                  "value": "Account Number: 123456"
                }
                
                If only the sort code is provided:
                
                User:
                My sort code is 55-55-66.
                
                Output:
                {
                  "intent": "STORE_MEMORY",
                  "memoryType": "OTHER",
                  "memoryTypeName": "BANK_ACCOUNT",
                  "title": "Bank Account",
                  "value": "Sort Code: 55-55-66"
                }
                
                User:
                My bank account number is 123456 and sort code is 55-55-66.
                
                Output:
                {
                  "intent": "STORE_MEMORY",
                  "memoryType": "OTHER",
                  "memoryTypeName": "BANK_ACCOUNT",
                  "title": "Bank Account",
                  "value": "Account Number: 123456; Sort Code: 55-55-66"
                }
                
                User:
                What is my bank account number and sort code?
                
                Output:
                {
                  "intent": "RETRIEVE_MEMORY",
                  "memoryType": "OTHER",
                  "memoryTypeName": "BANK_ACCOUNT",
                  "title": "Bank Account",
                  "value": null
                }
                
                ----------------------------------------

                OBJECT_LOCATION

                Use OBJECT_LOCATION when the user says
                where they placed an object.

                Example:

                I kept my cup in the kitchen.

                Output:

                {
                  "intent": "STORE_MEMORY",
                  "memoryType": "OBJECT_LOCATION",
                  "memoryTypeName": null,
                  "title": "Cup",
                  "value": "Kitchen"
                }

                Example:

                Where is my cup?

                Output:

                {
                  "intent": "RETRIEVE_MEMORY",
                  "memoryType": "OBJECT_LOCATION",
                  "memoryTypeName": null,
                  "title": "Cup",
                  "value": null
                }

                ----------------------------------------

                --------------------------------------------------
                OTHER MEMORY TYPES
                --------------------------------------------------
                
                If the information does not match one of the standard
                memory types, use:
                
                "memoryType": "OTHER"
                
                Put the specific category into:
                
                "memoryTypeName"
                
                Never create a new value for memoryType.
                
                Valid memoryType values are only:
                
                ADDRESS
                PHONE
                EMAIL
                PASSWORD
                OBJECT_LOCATION
                OTHER
                
                Examples:
                
                Passport number:
                
                {
                  "intent": "STORE_MEMORY",
                  "memoryType": "OTHER",
                  "memoryTypeName": "PASSPORT_NUMBER",
                  "title": "Passport Number",
                  "value": "P123456"
                }
                
                Insurance number:
                
                {
                  "intent": "STORE_MEMORY",
                  "memoryType": "OTHER",
                  "memoryTypeName": "INSURANCE_NUMBER",
                  "title": "Insurance Number",
                  "value": "ABC123"
                }
                
                --------------------------------------------------
                BANK ACCOUNT
                --------------------------------------------------
                
                Bank account information must use:
                
                "memoryType": "OTHER"
                
                "memoryTypeName": "BANK_ACCOUNT"
                
                "title": "Bank Account"
                
                If the user provides both account number and sort code,
                store both values together.
                
                Example:
                
                User:
                My bank account number is 123456 and sort code is 55-55-66.
                
                Output:
                
                {
                  "intent": "STORE_MEMORY",
                  "memoryType": "OTHER",
                  "memoryTypeName": "BANK_ACCOUNT",
                  "title": "Bank Account",
                  "value": "Account Number: 123456; Sort Code: 55-55-66"
                }
                
                If only the account number is provided:
                
                {
                  "intent": "STORE_MEMORY",
                  "memoryType": "OTHER",
                  "memoryTypeName": "BANK_ACCOUNT",
                  "title": "Bank Account",
                  "value": "Account Number: 123456"
                }
                
                If only the sort code is provided:
                
                {
                  "intent": "STORE_MEMORY",
                  "memoryType": "OTHER",
                  "memoryTypeName": "BANK_ACCOUNT",
                  "title": "Bank Account",
                  "value": "Sort Code: 55-55-66"
                }
                
                --------------------------------------------------
                RETRIEVING BANK ACCOUNT
                --------------------------------------------------
                
                Questions such as:
                
                What is my bank account number?
                
                What is my sort code?
                
                What are my bank account details?
                
                What is my account number and sort code?
                
                must use:
                
                "intent": "RETRIEVE_MEMORY"
                
                "memoryType": "OTHER"
                
                "memoryTypeName": "BANK_ACCOUNT"
                
                "title": "Bank Account"
                
                "value": null

                ========================================
                RETRIEVE_MEMORY
                ========================================

                For RETRIEVE_MEMORY:

                - value must always be null
                - identify the appropriate memoryType
                - identify the title
                - identify memoryTypeName when necessary

                Example:

                What is my phone number?

                Output:

                {
                  "intent": "RETRIEVE_MEMORY",
                  "memoryType": "PHONE",
                  "memoryTypeName": null,
                  "title": "Phone Number",
                  "value": null
                }

                ========================================
                DELETE_MEMORY
                ========================================

                For DELETE_MEMORY:

                - value must be null
                - identify the appropriate memoryType
                - identify the title

                Example:

                Forget my passport number.

                Output:

                {
                  "intent": "DELETE_MEMORY",
                  "memoryType": "OTHER",
                  "memoryTypeName": "PASSPORT_NUMBER",
                  "title": "Passport Number",
                  "value": null
                }
                ========================================
                RETRIEVE MEMORY RULE
                OBJECT_LOCATION is used when the memory refers to the
                location of a physical object.
                
                Physical objects include, but are not limited to:
                
                purse
                wallet
                bag
                handbag
                keys
                glasses
                passport
                phone
                coat
                shoes
                watch
                remote
                documents
                medicine
                umbrella
                jewellery
                laptop
                
                The object name itself does NOT need to appear in the examples.
                
                If the user asks to:
                
                - remember where an object is
                - retrieve where an object is
                - forget where an object is
                - delete the stored location of an object
                
                then use:
                
                OBJECT_LOCATION
                
                Examples:
                
                "Where is my purse?"
                → RETRIEVE_MEMORY + OBJECT_LOCATION
                
                "Where did I keep my purse?"
                → RETRIEVE_MEMORY + OBJECT_LOCATION
                
                "Forget my purse."
                → DELETE_MEMORY + OBJECT_LOCATION
                
                "Delete the location of my purse."
                → DELETE_MEMORY + OBJECT_LOCATION
                
                "I kept my purse in the bedroom."
                → STORE_MEMORY + OBJECT_LOCATION
                
                IMPORTANT:
                
                When the user says "Forget my purse", interpret this
                as forgetting the stored location of the purse if a
                purse location has previously been stored.
                
                Do NOT classify a physical object such as purse, wallet,
                bag, keys, glasses, passport, phone, or watch as OTHER
                when the context indicates that its stored location
                is being remembered, retrieved, or deleted.
                
                ========================================
                ========================================
                IMPORTANT CONTACT RULE
                ========================================

                This extractor handles ONLY the user's own memory.

                Another person's phone number is NOT a memory.

                Example:

                My daughter's phone number is 888888.

                This should NOT be processed here.

                It should be handled by ContactExtractor.

                ========================================
                FIELD RULES
                ========================================

                1. Always return all five fields.

                2. Never omit a field.

                3. Never return empty strings.

                4. Unknown fields must be null.

                5. RETRIEVE_MEMORY must have value = null.

                6. DELETE_MEMORY must have value = null.

                7. STORE_MEMORY must contain the stored value.

                8. title must be a readable display name.

                9. memoryTypeName must be null unless
                   memoryType is OTHER.

                10. Never create a new memoryType.

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

        System.out.println("====================================");
        System.out.println("Raw Memory AI Response:");
        System.out.println(rawResponse);
        System.out.println("====================================");

        try {

            return objectMapper.readValue(
                    rawResponse,
                    AIResponse.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to parse Memory AI response: "
                            + rawResponse,
                    e
            );
        }
    }
}