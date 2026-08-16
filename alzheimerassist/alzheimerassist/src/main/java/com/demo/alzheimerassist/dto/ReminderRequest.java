package com.demo.alzheimerassist.dto;

import com.demo.alzheimerassist.entity.RepeatType;
import lombok.Data;
import java.time.LocalDateTime;


@Data
public class ReminderRequest {

    private Long userId;

    private String title;

    private String description;

    private LocalDateTime reminderDateTime;

    private RepeatType repeatType;

    private String reminderText;

    public ReminderRequest() {
    }



}