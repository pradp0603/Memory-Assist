package com.demo.alzheimerassist.service.impl;

import com.demo.alzheimerassist.dto.ChatResponse;
import com.demo.alzheimerassist.dto.ContactResponse;
import com.demo.alzheimerassist.dto.MemoryResponse;
import com.demo.alzheimerassist.dto.ReminderResponse;
import com.demo.alzheimerassist.service.ResponseFormatterService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResponseFormatterServiceImpl
        implements ResponseFormatterService {

    @Override
    public ChatResponse formatMemory(MemoryResponse response) {

        if(response == null){

            return new ChatResponse(
                    "I couldn't find that information.");

        }

        return new ChatResponse(response.getValue());

    }

    @Override
    public ChatResponse formatContact(ContactResponse response) {

        if(response == null){

            return new ChatResponse(
                    "I couldn't find that contact.");

        }

        return new ChatResponse(

                response.getName()

                        + "'s phone number is "

                        + response.getPhoneNumber());

    }

    @Override
    public ChatResponse formatReminder(ReminderResponse reminder) {

        if(reminder==null){

            return new ChatResponse(
                    "Reminder not found.");

        }

        return new ChatResponse(

                reminder.getTitle()

                        + " at "

                        + reminder.getReminderDateTime());

    }

    @Override
    public ChatResponse formatReminderList(
            List<ReminderResponse> reminders){

        if(reminders.isEmpty()){

            return new ChatResponse(
                    "You don't have any reminders today.");

        }

        StringBuilder builder=new StringBuilder();

        builder.append("Today's reminders:\n\n");

        for(ReminderResponse reminder:reminders){

            builder.append("• ")

                    .append(reminder.getTitle())

                    .append(" at ")

                    .append(reminder.getReminderDateTime())

                    .append("\n");

        }

        return new ChatResponse(builder.toString());

    }

    @Override
    public ChatResponse formatError(String message){

        return new ChatResponse(message);

    }

    @Override
    public ChatResponse formatSuccess(String message){

        return new ChatResponse(message);

    }

}