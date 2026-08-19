package com.demo.alzheimerassist.dto;

import com.demo.alzheimerassist.entity.MemoryType;
import lombok.Data;

@Data
public class MemoryRequest {

    private Long userId;

    private MemoryType memoryType;

    private String title;

    private String value;

    private String memoryTypeName;


}
