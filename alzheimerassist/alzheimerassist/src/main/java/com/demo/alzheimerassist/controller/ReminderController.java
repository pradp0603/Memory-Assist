package com.demo.alzheimerassist.controller;

import com.demo.alzheimerassist.dto.ReminderRequest;
import com.demo.alzheimerassist.dto.ReminderResponse;
import com.demo.alzheimerassist.service.ReminderService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderService service;

    public ReminderController(ReminderService service) {
        this.service = service;
    }

    @PostMapping
    public ReminderResponse save(@RequestBody ReminderRequest request) {

        return service.save(request);
    }

    @GetMapping("/{id}")
    public ReminderResponse get(@PathVariable Long id) {

        return service.get(id);
    }

    @GetMapping("/user/{userId}")
    public List<ReminderResponse> getAll(@PathVariable Long userId) {

        return service.getAll(userId);
    }

    @GetMapping("/today/{userId}")
    public List<ReminderResponse> getTodaysReminders(@PathVariable Long userId) {

        return service.getTodaysReminders(userId);
    }

    @PutMapping("/{id}")
    public ReminderResponse update(@PathVariable Long id, @RequestBody ReminderRequest request) {

        return service.update(id, request);
    }

    @PutMapping("/{id}/complete")
    public ReminderResponse complete(@PathVariable Long id) {

        return service.markCompleted(id);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {

        service.delete(id);

        return "Reminder deleted successfully";
    }

}