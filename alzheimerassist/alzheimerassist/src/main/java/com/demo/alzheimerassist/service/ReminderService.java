package com.demo.alzheimerassist.service;

import com.demo.alzheimerassist.dto.ReminderRequest;
import com.demo.alzheimerassist.dto.ReminderResponse;

import java.time.LocalDate;
import java.util.List;

public interface ReminderService {

    ReminderResponse save(ReminderRequest request);

    ReminderResponse update(Long id, ReminderRequest request);

    void delete(Long id);

    ReminderResponse get(Long id);

    List<ReminderResponse> getAll(Long userId);

    List<ReminderResponse> getTodaysReminders(Long userId);

    ReminderResponse markCompleted(Long id);

    ReminderResponse createReminder(ReminderRequest request);

    void markReminderCompleted(Long userId, String reminderText);

    List<ReminderResponse> getRemindersForDate( Long userId, LocalDate date);

}