package com.demo.alzheimerassist.ai;

import com.demo.alzheimerassist.dto.AIResponse;


public interface ReminderExtractor {

    AIResponse extract(String message, String intent);

}