package com.demo.alzheimerassist.service.impl;

import com.demo.alzheimerassist.dto.ChatResponse;
import com.demo.alzheimerassist.dto.ReminderRequest;
import com.demo.alzheimerassist.dto.ReminderResponse;
import com.demo.alzheimerassist.entity.Reminder;
import com.demo.alzheimerassist.entity.ReminderStatus;
import com.demo.alzheimerassist.entity.RepeatType;
import com.demo.alzheimerassist.entity.User;
import com.demo.alzheimerassist.exception.ResourceNotFoundException;
import com.demo.alzheimerassist.repository.ReminderRepository;
import com.demo.alzheimerassist.repository.UserRepository;
import com.demo.alzheimerassist.service.ReminderService;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ReminderServiceImpl implements ReminderService {
    private final ReminderRepository reminderRepository;

    private final UserRepository userRepository;

    public ReminderServiceImpl(ReminderRepository reminderRepository, UserRepository userRepository){
        this.reminderRepository=reminderRepository;
        this.userRepository=userRepository;
    }

    @Override
    public ReminderResponse save(ReminderRequest request){

        User user=userRepository.findById(request.getUserId())
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));

        Reminder reminder=new Reminder();

        reminder.setUser(user);

        reminder.setTitle(request.getTitle());

        reminder.setDescription(request.getDescription());

        reminder.setReminderDateTime(request.getReminderDateTime());

        reminder.setRepeatType(request.getRepeatType());

        Reminder saved=reminderRepository.save(reminder);

        return convert(saved);

    }

    @Override
    public List<ReminderResponse> getAll(Long userId){

        return reminderRepository.findByUser_Id(userId)
                .stream()
                .map(this::convert)
                .toList();
    }

    @Override
    public ReminderResponse get(Long id){
        Reminder reminder=reminderRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Reminder not found"));

        return convert(reminder);

    }

    @Override
    public ReminderResponse update(Long id, ReminderRequest request){

        Reminder reminder=reminderRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Reminder not found"));

        reminder.setTitle(request.getTitle());

        reminder.setDescription(request.getDescription());

        reminder.setReminderDateTime(request.getReminderDateTime());

        reminder.setRepeatType(request.getRepeatType());

        return convert(reminderRepository.save(reminder));

    }

    @Override
    public void delete(Long id){
        reminderRepository.deleteById(id);
    }

    @Override
    public List<ReminderResponse> getTodaysReminders(Long userId) {

        List<Reminder> reminders = reminderRepository.findByUser_Id(userId);

        LocalDate today = LocalDate.now();
        DayOfWeek todayDay = today.getDayOfWeek();

        List<ReminderResponse> responseList = new ArrayList<>();

        for (Reminder reminder : reminders) {

            LocalDate reminderDate = reminder.getReminderDateTime().toLocalDate();

            switch (reminder.getRepeatType()) {

                case NONE -> {

                    if (reminderDate.equals(today)) {
                        responseList.add(convert(reminder));
                    }

                }

                case DAILY -> {

                    responseList.add(convert(reminder));

                }

                case WEEKLY -> {

                    if (reminderDate.getDayOfWeek().equals(todayDay)) {
                        responseList.add(convert(reminder));
                    }

                }

                case MONTHLY -> {

                    if (reminderDate.getDayOfMonth() == today.getDayOfMonth()) {
                        responseList.add(convert(reminder));
                    }

                }

                case YEARLY -> {

                    if (reminderDate.getMonth() == today.getMonth()
                            &&
                            reminderDate.getDayOfMonth() == today.getDayOfMonth()) {

                        responseList.add(convert(reminder));
                    }

                }

            }

        }

        return responseList;

    }

    @Override
    public ReminderResponse markCompleted(Long id){

        Reminder reminder=reminderRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Reminder not found"));

        reminder.setStatus(ReminderStatus.COMPLETED);

        return convert(reminderRepository.save(reminder));

    }

    @Override
    public ReminderResponse createReminder(ReminderRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Reminder reminder = new Reminder();

        reminder.setUser(user);
        reminder.setReminderText(request.getReminderText());
        reminder.setReminderDateTime(request.getReminderDateTime());
        reminder.setRepeatType(request.getRepeatType());

        Reminder saved = reminderRepository.save(reminder);

        return convert(saved);
    }


    @Override
    public void markReminderCompleted(
            Long userId,
            String reminderText) {

        List<Reminder> reminders =
                reminderRepository
                        .findByUser_IdAndStatusAndReminderTextContainingIgnoreCase(
                                userId,
                                ReminderStatus.PENDING,
                                reminderText
                        );

        if (reminders.isEmpty()) {
            throw new ResourceNotFoundException(
                    "I couldn't find any pending reminder."
            );
        }

        LocalDate today = LocalDate.now();

        Reminder reminder = reminders.stream()
                .filter(r -> {

                    if (r.getReminderDateTime() == null) {
                        return false;
                    }

                    LocalDate reminderDate =
                            r.getReminderDateTime().toLocalDate();

                    return reminderDate.equals(today)
                            || r.getRepeatType() == RepeatType.DAILY;
                })
                .min(
                        Comparator.comparing(
                                Reminder::getReminderDateTime
                        )
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "I couldn't find any pending reminder."
                        )
                );

        /*
         * For a recurring reminder we don't want to destroy
         * the recurring reminder itself.
         *
         * We only mark today's occurrence as completed.
         */
        if (reminder.getRepeatType() == RepeatType.DAILY) {

            // Keep the recurring reminder pending.
            // The next daily occurrence should still exist.

            return;
        }

        reminder.setStatus(ReminderStatus.COMPLETED);

        reminderRepository.save(reminder);
    }


    @Override
    public List<ReminderResponse> getRemindersForDate(
            Long userId,
            LocalDate date) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        // 1. Get normal one-time reminders for this date
        List<Reminder> reminders =
                reminderRepository
                        .findByUser_IdAndStatusAndReminderDateTimeBetween(
                                userId,
                                ReminderStatus.PENDING,
                                start,
                                end
                        );

        // 2. Get all DAILY reminders
        List<Reminder> dailyReminders =
                reminderRepository
                        .findByUser_IdAndStatusAndRepeatType(
                                userId,
                                ReminderStatus.PENDING,
                                RepeatType.DAILY
                        );

        // 3. Add DAILY reminders that are not already present
        for (Reminder daily : dailyReminders) {

            boolean alreadyExists =
                    reminders.stream()
                            .anyMatch(r ->
                                    r.getId().equals(daily.getId())
                            );

            if (!alreadyExists) {
                reminders.add(daily);
            }
        }

        // 4. Sort by reminder time

        reminders.sort(
                Comparator.comparing(reminder ->
                        reminder.getReminderDateTime().toLocalTime()
                )
        );


        return reminders.stream()
                .map(this::convert)
                .toList();
    }


    private ReminderResponse convert(Reminder reminder){

        ReminderResponse response=new ReminderResponse();

        response.setId(reminder.getId());

        response.setTitle(reminder.getTitle());

        response.setDescription(reminder.getDescription());

        response.setReminderDateTime(reminder.getReminderDateTime());

        response.setRepeatType(reminder.getRepeatType());

        response.setStatus(reminder.getStatus());

        response.setReminderText(reminder.getReminderText());

        return response;

    }
}
