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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemoryServiceImpl implements MemoryService {

    private final MemoryRepository memoryRepository;
    private final UserRepository userRepository;

    public MemoryServiceImpl(MemoryRepository memoryRepository, UserRepository userRepository) {

        this.memoryRepository = memoryRepository;
        this.userRepository = userRepository;
    }


    @Override
    public MemoryResponse saveMemory(MemoryRequest request) {

        Memory memory;

		if (request.getMemoryType() == MemoryType.OBJECT_LOCATION) {

			memory = memoryRepository.findByUser_IdAndTypeAndTitleIgnoreCase(
							request.getUserId(),
							request.getMemoryType(),
							request.getTitle())
					.orElse(new Memory());

		} else {

			memory = memoryRepository.findByUser_IdAndType(
							request.getUserId(),
							request.getMemoryType())
					.orElse(new Memory());
		}

        if (memory.getId() == null) {

            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("User not found"));

            memory.setUser(user);
            memory.setCreatedDate(LocalDateTime.now());

        }

        memory.setType(request.getMemoryType());
        memory.setMemoryTypeName(request.getMemoryTypeName());
        memory.setTitle(request.getTitle());
        memory.setValue(request.getValue());
        memory.setUpdatedDate(LocalDateTime.now());

        Memory saved = memoryRepository.save(memory);

        return convertToResponse(saved);

    }

    @Override
    public List<MemoryResponse> getAllMemories(Long userId) {
        List<Memory> memories = memoryRepository.findByUser_Id(userId);

        return memories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MemoryResponse getMemory(Long userId, MemoryType type) {
        return null;
    }

    public MemoryResponse getOtherMemory(
            Long userId,
            String memoryTypeName) {

        Memory memory = memoryRepository
                .findByUser_IdAndTypeAndMemoryTypeNameIgnoreCase(
                        userId,
                        MemoryType.OTHER,
                        memoryTypeName)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "I couldn't find that information."));

        return convertToResponse(memory);
    }

    @Override
    public MemoryResponse getMemory(Long userId, MemoryType memoryType, String title) {

        Memory memory;

        if (memoryType == MemoryType.OBJECT_LOCATION || memoryType == MemoryType.OTHER) {

            memory = memoryRepository
                    .findByUser_IdAndTypeAndTitleIgnoreCase(
                            userId,
                            memoryType,
                            title)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Memory not found"));

        } else {

            memory = memoryRepository
                    .findByUser_IdAndType(
                            userId,
                            memoryType)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Memory not found"));
        }

        if (memory == null) {
            return null;
        }
        return convertToResponse(memory);
    }

    @Override
    public MemoryResponse updateMemory(Long id, MemoryRequest request) {

        Memory memory = memoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Memory not found"));

        memory.setType(request.getMemoryType());
        memory.setTitle(request.getTitle());
        memory.setMemoryTypeName(request.getMemoryTypeName());
        memory.setValue(request.getValue());

        Memory updated = memoryRepository.save(memory);

        return convertToResponse(updated);
    }

    @Override
    public void deleteMemory(Long id) {

        if(!memoryRepository.existsById(id)) {

            throw new ResourceNotFoundException("Memory not found");

        }

        memoryRepository.deleteById(id);

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
        response.setMemoryTypeName(memory.getMemoryTypeName());

        return response;
    }

    @Override
    public MemoryResponse getObjectLocation(Long userId, String objectName) {

        Memory memory = memoryRepository.findByUser_IdAndTypeAndTitleIgnoreCase(userId, MemoryType.OBJECT_LOCATION, objectName)
                        .orElseThrow(() ->new ResourceNotFoundException("Object location not found"));

        return convertToResponse(memory);
    }

    @Override
    public void deleteMemory(Long userId, MemoryType memoryType, String title) {

        Memory memory;

        if (memoryType == MemoryType.OBJECT_LOCATION
                || memoryType == MemoryType.OTHER) {

            memory = memoryRepository
                    .findByUser_IdAndTypeAndTitleIgnoreCase(
                            userId,
                            memoryType,
                            title)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Memory not found"));

        } else {

            memory = memoryRepository
                    .findByUser_IdAndType(
                            userId,
                            memoryType)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Memory not found"));
        }

        memoryRepository.delete(memory);
    }

}
