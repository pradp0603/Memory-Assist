package com.demo.alzheimerassist.controller;

import com.demo.alzheimerassist.entity.MemoryType;
import org.springframework.web.bind.annotation.*;
import com.demo.alzheimerassist.dto.MemoryRequest;
import com.demo.alzheimerassist.dto.MemoryResponse;
import com.demo.alzheimerassist.service.MemoryService;
import java.util.List;

@RestController
@RequestMapping("/api/memory")
public class MemoryController {

    private final MemoryService service;

    public MemoryController(MemoryService service) {
        this.service = service;
    }

    @PostMapping
    public MemoryResponse saveMemory(@RequestBody MemoryRequest request) {

        return service.saveMemory(request);
    }

    @GetMapping("/{userId}")
    public List<MemoryResponse> getAllMemories(@PathVariable Long userId) {

        return service.getAllMemories(userId);
    }

    @GetMapping("/{userId}/{type}")
    public MemoryResponse getMemory(@PathVariable Long userId, @PathVariable MemoryType type) {

        return service.getMemory(userId, type);
    }

    @PutMapping("/{id}")
    public MemoryResponse updateMemory(@PathVariable Long id, @RequestBody MemoryRequest request) {

        return service.updateMemory(id, request);

    }

    @DeleteMapping("/{id}")
    public String deleteMemory(@PathVariable Long id) {

        service.deleteMemory(id);

        return "Memory deleted successfully";
    }

}