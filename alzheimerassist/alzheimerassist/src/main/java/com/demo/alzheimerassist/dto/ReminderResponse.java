package com.demo.alzheimerassist.dto;

import com.demo.alzheimerassist.entity.RepeatType;

import com.demo.alzheimerassist.entity.ReminderStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReminderResponse {

    private Long id;

    private String title;

    private String description;

    private LocalDateTime reminderDateTime;

    private RepeatType repeatType;

    private ReminderStatus status;



}