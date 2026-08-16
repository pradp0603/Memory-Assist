package com.demo.alzheimerassist.service;

import com.demo.alzheimerassist.dto.ChatResponse;
import com.demo.alzheimerassist.dto.ContactResponse;
import com.demo.alzheimerassist.dto.MemoryResponse;
import com.demo.alzheimerassist.dto.ReminderResponse;

import java.util.List;

public interface ResponseFormatterService {

    ChatResponse formatMemory(MemoryResponse response);

    ChatResponse formatContact(ContactResponse response);

    ChatResponse formatReminder(ReminderResponse response);

    ChatResponse formatReminderList(List<ReminderResponse> reminders);

    ChatResponse formatError(String message);

    ChatResponse formatSuccess(String message);

    ChatResponse memorySaved(MemoryResponse memory);

    ChatResponse memoryRetrieved(MemoryResponse memory);

    ChatResponse notFound(String item);

    ChatResponse todaysReminders(List<ReminderResponse> reminders);

}