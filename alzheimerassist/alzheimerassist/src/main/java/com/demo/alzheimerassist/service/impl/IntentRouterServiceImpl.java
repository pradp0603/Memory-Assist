package com.demo.alzheimerassist.service.impl;

import com.demo.alzheimerassist.dto.*;
import com.demo.alzheimerassist.entity.MemoryType;
import com.demo.alzheimerassist.entity.RelationshipType;
import com.demo.alzheimerassist.exception.MissingContactNameException;
import com.demo.alzheimerassist.exception.ResourceNotFoundException;
import com.demo.alzheimerassist.service.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class IntentRouterServiceImpl implements IntentRouterService {

    private final MemoryService memoryService;

    private final ContactService contactService;

    private final ReminderService reminderService;

    private final ResponseFormatterService responseFormatterService;

    public IntentRouterServiceImpl(MemoryService memoryService, ContactService contactService, ReminderService reminderService, ResponseFormatterService responseFormatterService){

        this.memoryService=memoryService;

        this.contactService=contactService;

        this.reminderService=reminderService;

        this.responseFormatterService=responseFormatterService;

    }

    @Override
    public ChatResponse route(
            Long userId,
            String message,
            AIResponse aiResponse) {

        if (aiResponse == null ||
                aiResponse.getIntent() == null) {

            throw new RuntimeException(
                    "Intent cannot be null"
            );
        }

        return switch (aiResponse.getIntent()) {

            case STORE_MEMORY ->
                    storeMemory(userId, aiResponse);

            case RETRIEVE_MEMORY ->
                    retrieveMemory(userId, aiResponse);

            case DELETE_MEMORY ->
                    deleteMemory(userId, aiResponse);

            case STORE_CONTACT ->
                    storeContact(userId, aiResponse);

            case GET_CONTACT ->
                    getContact(userId, aiResponse);

            case DELETE_CONTACT ->
                    deleteContact(userId, aiResponse);

            case UPDATE_CONTACT ->
                    updateContact(userId, aiResponse);

            case STORE_REMINDER ->
                    storeReminder(userId, message, aiResponse);

            case GET_TODAYS_REMINDERS ->
                    getTodaysReminders(userId, aiResponse);

            case MARK_REMINDER_COMPLETE ->
                    reminderCompleted(userId, aiResponse);

            default ->
                    new ChatResponse(
                            "I didn't understand your request."
                    );
        };
    }


    private ChatResponse storeMemory(Long userId, AIResponse aiResponse) {

        MemoryRequest memoryRequest = new MemoryRequest();

        memoryRequest.setUserId(userId);
        memoryRequest.setMemoryType(aiResponse.getMemoryType());
        memoryRequest.setTitle(aiResponse.getTitle());
        memoryRequest.setValue(aiResponse.getValue());
        memoryRequest.setMemoryTypeName(aiResponse.getMemoryTypeName());

        memoryService.saveMemory(memoryRequest);

        return new ChatResponse("I've saved that information.");

    }

    private ChatResponse retrieveMemory(Long userId, AIResponse aiResponse) {

        if (aiResponse.getMemoryType() == MemoryType.OTHER && aiResponse.getMemoryTypeName() != null) {

            MemoryResponse response = memoryService.getOtherMemory(userId, aiResponse.getMemoryTypeName());

            return new ChatResponse(response.getValue());
        }

        if (aiResponse.getMemoryType() == MemoryType.OBJECT_LOCATION) {

            MemoryResponse response = memoryService.getObjectLocation(userId, aiResponse.getTitle());

            return new ChatResponse("Your " + response.getTitle()+ " is in " + response.getValue());

        }

        try {

            MemoryResponse response = memoryService.getMemory(
                    userId,
                    aiResponse.getMemoryType(),
                    aiResponse.getTitle());

            return new ChatResponse(response.getValue());

        } catch (ResourceNotFoundException ex) {

            return new ChatResponse("I couldn't find that information.");
        }
    }

    private ChatResponse storeContact(Long userId,
                                      AIResponse aiResponse) {

        ContactRequest contactRequest = new ContactRequest();

        if (aiResponse.getContactName() == null || aiResponse.getContactName().isBlank()) {
            throw new MissingContactNameException("Please provide the person's name along with the phone number so I can save the contact.");
        }

        if (aiResponse.getPhoneNumber() == null || aiResponse.getPhoneNumber().isBlank()) {
            throw new MissingContactNameException("Please provide the person's phone number along with the name so I can save the contact.");
        }

        contactRequest.setUserId(userId);
        contactRequest.setName(aiResponse.getContactName());
        contactRequest.setPhoneNumber(aiResponse.getPhoneNumber());
        contactRequest.setRelationship(aiResponse.getRelationship());

        ContactResponse response = contactService.saveContact(contactRequest);

        return new ChatResponse("I've saved " + response.getName() + "'s phone number.");
    }

    private ChatResponse getContact(Long userId, AIResponse aiResponse) {

        ContactResponse response;

        if (aiResponse.getRelationship() != null) {
            if (aiResponse.getRelationship() == RelationshipType.OTHER) {

                return new ChatResponse("I couldn't find that contact.");
            }

            response = contactService.getContactByRelationship(userId, aiResponse.getRelationship());

        } else {
            response = contactService.getContactByName(userId, aiResponse.getContactName());
        }

        return new ChatResponse(response.getName() + "'s phone number is "+ response.getPhoneNumber()
        );
    }

    private ChatResponse storeReminder(Long userId, String message,
                                       AIResponse aiResponse) {

        ReminderRequest reminderRequest = new ReminderRequest();

        String reminderText = aiResponse.getReminderText();
        if (reminderText == null || reminderText.isBlank()) {
            reminderText = message;
        }

        reminderRequest.setReminderText(reminderText);
        reminderRequest.setUserId(userId);
        reminderRequest.setReminderDateTime(aiResponse.getReminderDateTime());
        reminderRequest.setRepeatType(aiResponse.getRepeatType());

        ReminderResponse response = reminderService.createReminder(reminderRequest);

        return new ChatResponse(
                "I've created a reminder: "
                        + response.getReminderText()
                        + " at "
                        + response.getReminderDateTime()
        );
    }



    private ChatResponse getTodaysReminders(Long userId, AIResponse aiResponse) {

        LocalDate requestedDate;

        if (aiResponse.getReminderDateTime() != null) {
            requestedDate = aiResponse.getReminderDateTime().toLocalDate();
        } else {
            requestedDate = LocalDate.now();
        }

        List<ReminderResponse> reminders =
                reminderService.getRemindersForDate(
                        userId,
                        requestedDate);

        return responseFormatterService.todaysReminders(reminders);
    }

    private ChatResponse deleteMemory(Long userId,
                                      AIResponse aiResponse) {

        try {

            memoryService.deleteMemory(
                    userId,
                    aiResponse.getMemoryType(),
                    aiResponse.getTitle());

            return new ChatResponse(
                    "I've forgotten that information.");

        } catch (ResourceNotFoundException ex) {

            return new ChatResponse(
                    "I couldn't find that information.");
        }
    }

    private ChatResponse deleteContact(Long userId,
                                       AIResponse aiResponse) {

        try {

            contactService.deleteContact(
                    userId,
                    aiResponse.getContactName(),
                    aiResponse.getRelationship());

            return new ChatResponse("I've deleted that contact.");

        } catch (ResourceNotFoundException ex) {

            return new ChatResponse("I couldn't find that contact.");
        }
    }

    private ChatResponse updateContact(Long userId, AIResponse aiResponse) {

        try {

            ContactRequest contactRequest = new ContactRequest();

            if (aiResponse.getContactName() == null || aiResponse.getContactName().isBlank()) {
                throw new MissingContactNameException("Please provide the person's name along with the phone number so I can save the contact.");
            }

            if (aiResponse.getPhoneNumber() == null || aiResponse.getPhoneNumber().isBlank()) {
                throw new MissingContactNameException("Please provide the person's phone number along with the name so I can save the contact.");
            }

            contactRequest.setUserId(userId);
            contactRequest.setName(aiResponse.getContactName());
            contactRequest.setPhoneNumber(aiResponse.getPhoneNumber());
            contactRequest.setRelationship(aiResponse.getRelationship());

            ContactResponse response = contactService.updateContact(contactRequest, aiResponse);

            return new ChatResponse("I've updated " + response.getName() + "'s phone number.");

        } catch (ResourceNotFoundException ex) {

            return new ChatResponse( "I couldn't find that contact.");
        }
    }

    private ChatResponse getReminders(
            Long userId,
            AIResponse aiResponse) {


        LocalDateTime dateTime = aiResponse.getReminderDateTime();

        if (dateTime == null) {
            throw new IllegalArgumentException(
                    "Reminder date is required"
            );
        }

        LocalDate date = dateTime.toLocalDate();

        List<ReminderResponse> reminders =
                reminderService.getRemindersForDate(
                        userId,
                        date
                );

        if (reminders == null || reminders.isEmpty()) {

            return new ChatResponse(
                    "You don't have any reminders for "
                            + formatDate(date)
                            + "."
            );
        }

        StringBuilder response = new StringBuilder();

        response.append(
                "Your reminders for "
                        + formatDate(date)
                        + ":\n\n"
        );

        for (ReminderResponse reminder : reminders) {

            String reminderName = reminder.getTitle();

            if (reminderName == null || reminderName.isBlank()) {
                reminderName = reminder.getReminderText();
            }

            if (reminderName == null || reminderName.isBlank()) {
                reminderName = "Reminder";
            }

            response.append("• ")
                    .append(reminderName)
                    .append(" at ")
                    .append(
                            reminder.getReminderDateTime()
                                    .toLocalTime()
                    )
                    .append("\n");
        }

        return new ChatResponse(
                response.toString()
        );
    }

    /* TODO : This can be optimised by adding a if condition (if (reminder.getRepeatType() != RepeatType.NONE) as status will be updated only for Repeat type = none*/

    private ChatResponse reminderCompleted(Long userId, AIResponse aiResponse) {

        try {

            reminderService.markReminderCompleted(
                    userId,
                    aiResponse.getReminderText());

            return new ChatResponse("Great! I've marked your reminder as completed.");

        } catch (ResourceNotFoundException ex) {

            return new ChatResponse("I couldn't find any pending reminder.");

        }

    }

    private String formatDate(LocalDate date) {

        LocalDate today = LocalDate.now();

        if (date.equals(today)) {
            return "today";
        }

        if (date.equals(today.plusDays(1))) {
            return "tomorrow";
        }

        return date.toString();
    }

}