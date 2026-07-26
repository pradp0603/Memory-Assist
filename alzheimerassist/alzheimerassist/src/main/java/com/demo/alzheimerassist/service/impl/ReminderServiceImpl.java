package com.demo.alzheimerassist.service.impl;

import com.demo.alzheimerassist.dto.ReminderRequest;
import com.demo.alzheimerassist.dto.ReminderResponse;
import com.demo.alzheimerassist.entity.Reminder;
import com.demo.alzheimerassist.entity.ReminderStatus;
import com.demo.alzheimerassist.entity.User;
import com.demo.alzheimerassist.exception.ResourceNotFoundException;
import com.demo.alzheimerassist.repository.ReminderRepository;
import com.demo.alzheimerassist.repository.UserRepository;
import com.demo.alzheimerassist.service.ReminderService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReminderServiceImpl implements ReminderService {
    private final ReminderRepository repository;

    private final UserRepository userRepository;

    public ReminderServiceImpl(ReminderRepository repository, UserRepository userRepository){
        this.repository=repository;
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

        Reminder saved=repository.save(reminder);

        return convert(saved);

    }

    @Override
    public List<ReminderResponse> getAll(Long userId){

        return repository.findByUser_Id(userId)
                .stream()
                .map(this::convert)
                .toList();
    }

    @Override
    public ReminderResponse get(Long id){
        Reminder reminder=repository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Reminder not found"));

        return convert(reminder);

    }

    @Override
    public ReminderResponse update(Long id, ReminderRequest request){

        Reminder reminder=repository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Reminder not found"));

        reminder.setTitle(request.getTitle());

        reminder.setDescription(request.getDescription());

        reminder.setReminderDateTime(request.getReminderDateTime());

        reminder.setRepeatType(request.getRepeatType());

        return convert(repository.save(reminder));

    }

    @Override
    public void delete(Long id){
        repository.deleteById(id);
    }

    @Override
    public List<ReminderResponse> getTodaysReminders(Long userId){
        LocalDate today=LocalDate.now();
        LocalDateTime start=today.atStartOfDay();
        LocalDateTime end=today.atTime(23,59,59);

        return repository.findByUser_IdAndReminderDateTimeBetween(userId, start, end)
                .stream()
                .map(this::convert)
                .toList();
    }

    @Override
    public ReminderResponse markCompleted(Long id){

        Reminder reminder=repository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Reminder not found"));

        reminder.setStatus(ReminderStatus.COMPLETED);

        return convert(repository.save(reminder));

    }

    private ReminderResponse convert(Reminder reminder){

        ReminderResponse response=new ReminderResponse();

        response.setId(reminder.getId());

        response.setTitle(reminder.getTitle());

        response.setDescription(reminder.getDescription());

        response.setReminderDateTime(reminder.getReminderDateTime());

        response.setRepeatType(reminder.getRepeatType());

        response.setStatus(reminder.getStatus());

        return response;

    }
}
