package com.demo.alzheimerassist.dto;


import com.demo.alzheimerassist.entity.MemoryType;
import lombok.Data;

@Data
public class MemoryResponse {

    private Long id;

    private MemoryType type;

    private String title;

    private String value;

    // Getters and Setters
}
