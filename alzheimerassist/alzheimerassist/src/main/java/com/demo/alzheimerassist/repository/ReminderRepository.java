package com.demo.alzheimerassist.repository;

import com.demo.alzheimerassist.entity.Reminder;
import com.demo.alzheimerassist.entity.ReminderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReminderRepository
        extends JpaRepository<Reminder, Long> {

    List<Reminder> findByUser_Id(Long userId);

    List<Reminder> findByUser_IdAndReminderDateTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);

    List<Reminder> findByReminderDateTimeBeforeAndStatus(LocalDateTime dateTime, ReminderStatus status);

}