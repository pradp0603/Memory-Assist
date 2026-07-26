package com.demo.alzheimerassist.service.impl;


import com.demo.alzheimerassist.entity.User;
import com.demo.alzheimerassist.exception.ResourceNotFoundException;
import com.demo.alzheimerassist.repository.UserRepository;
import org.springframework.stereotype.Service;

import com.demo.alzheimerassist.dto.MemoryRequest;
import com.demo.alzheimerassist.dto.MemoryResponse;
import com.demo.alzheimerassist.entity.Memory;
import com.demo.alzheimerassist.repository.MemoryRepository;
import com.demo.alzheimerassist.service.MemoryService;
import com.demo.alzheimerassist.entity.MemoryType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemoryServiceImpl implements MemoryService {

    private final MemoryRepository repository;
    private final UserRepository userRepository;

    public MemoryServiceImpl(MemoryRepository repository, UserRepository userRepository) {

        this.repository = repository;
        this.userRepository = userRepository;
    }


    @Override
    public MemoryResponse saveMemory(MemoryRequest request) {

        Memory memory = new Memory();

        User user = userRepository.findById(request.getUserId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("User not found"));

        memory.setUser(user);

        memory.setType(request.getType());
        memory.setTitle(request.getTitle());
        memory.setValue(request.getValue());

        Memory saved = repository.save(memory);

        MemoryResponse response = new MemoryResponse();

        response.setId(saved.getId());
        response.setType(saved.getType());
        response.setTitle(saved.getTitle());
        response.setValue(saved.getValue());

        return response;
    }

    @Override
    public List<MemoryResponse> getAllMemories(Long userId) {
        List<Memory> memories = repository.findByUser_Id(userId);

        return memories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MemoryResponse getMemory(Long userId, MemoryType type) {

        Optional<Memory> memory = repository.findByUser_IdAndType(userId, type);

        return memory.map(this::convertToResponse).orElse(null);
    }

    @Override
    public MemoryResponse updateMemory(Long id, MemoryRequest request) {

        Memory memory = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Memory not found"));

        memory.setType(request.getType());
        memory.setTitle(request.getTitle());
        memory.setValue(request.getValue());

        Memory updated = repository.save(memory);

        return convertToResponse(updated);
    }

    @Override
    public void deleteMemory(Long id) {

        if(!repository.existsById(id)) {

            throw new ResourceNotFoundException("Memory not found");

        }

        repository.deleteById(id);

    }

    @Override
    public Optional<Memory> findByUser_IdAndTitleIgnoreCase(Long userId, String title) {
        return Optional.empty();
    }


    private MemoryResponse convertToResponse(Memory memory) {

        MemoryResponse response = new MemoryResponse();

        response.setId(memory.getId());
        response.setType(memory.getType());
        response.setTitle(memory.getTitle());
        response.setValue(memory.getValue());

        return response;
    }
}
