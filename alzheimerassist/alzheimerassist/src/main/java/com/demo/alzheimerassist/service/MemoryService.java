package com.demo.alzheimerassist.service;

import java.util.List;
import java.util.Optional;

import com.demo.alzheimerassist.dto.MemoryRequest;
import com.demo.alzheimerassist.dto.MemoryResponse;
import com.demo.alzheimerassist.entity.Memory;
import com.demo.alzheimerassist.entity.MemoryType;

public interface MemoryService {

    MemoryResponse saveMemory(MemoryRequest request);

    List<MemoryResponse> getAllMemories(Long userId);

    MemoryResponse getMemory(Long userId, MemoryType type);

    MemoryResponse getMemory(Long userId, MemoryType type, String title);

    MemoryResponse updateMemory(Long id, MemoryRequest request);

    void deleteMemory(Long id);

    Optional<Memory> findByUser_IdAndTitleIgnoreCase(Long userId, String title);

    MemoryResponse getObjectLocation(Long userId, String objectName);

    void deleteMemory(Long userId, MemoryType memoryType, String title);

    MemoryResponse getOtherMemory(Long userId, String memoryTypeName);

}
