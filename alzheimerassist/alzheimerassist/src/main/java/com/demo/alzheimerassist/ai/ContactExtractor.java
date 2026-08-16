package com.demo.alzheimerassist.ai;


import com.demo.alzheimerassist.dto.AIResponse;


public interface ContactExtractor {

    AIResponse extract(String message, String intent);

}