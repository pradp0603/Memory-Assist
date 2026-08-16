package com.demo.alzheimerassist.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.alzheimerassist.entity.Memory;
import com.demo.alzheimerassist.entity.MemoryType;

public interface MemoryRepository extends JpaRepository<Memory, Long> {

    List<Memory> findByUser_Id(Long userId);

    Optional<Memory> findByUser_IdAndType(Long userId, MemoryType type);

    Optional<Memory> findByUser_IdAndTypeAndTitleIgnoreCase(Long userId, MemoryType type, String title);

}