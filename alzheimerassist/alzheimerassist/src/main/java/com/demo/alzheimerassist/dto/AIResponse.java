package com.demo.alzheimerassist.dto;

import java.time.LocalDateTime;

import com.demo.alzheimerassist.entity.IntentType;
import com.demo.alzheimerassist.entity.MemoryType;
import com.demo.alzheimerassist.entity.RelationshipType;
import com.demo.alzheimerassist.entity.RepeatType;
import lombok.Data;

@Data
public class AIResponse {

    private IntentType intent;

    private MemoryType memoryType;

    private String title;

    private String value;

    // Contact fields
    private String contactName;

    private String phoneNumber;

    private RelationshipType relationship;

    // Reminder fields
    private LocalDateTime reminderDateTime;

    private RepeatType repeatType;


}