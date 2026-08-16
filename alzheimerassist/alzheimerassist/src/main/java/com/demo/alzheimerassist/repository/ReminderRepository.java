package com.demo.alzheimerassist.repository;

import com.demo.alzheimerassist.entity.Reminder;
import com.demo.alzheimerassist.entity.ReminderStatus;
import com.demo.alzheimerassist.entity.RepeatType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReminderRepository
        extends JpaRepository<Reminder, Long> {

    List<Reminder> findByUser_Id(Long userId);

    List<Reminder> findByUser_IdAndReminderDateTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);

    List<Reminder> findByReminderDateTimeBeforeAndStatus(LocalDateTime dateTime, ReminderStatus status);

    Optional<Reminder> findFirstByUser_IdAndStatusOrderByReminderDateTimeDesc(Long userId, ReminderStatus status);

    Optional<Reminder> findFirstByUser_IdAndReminderTextContainingIgnoreCaseAndStatusAndReminderDateTimeBetweenOrderByReminderDateTimeDesc(
            Long userId,
            String reminderText,
            ReminderStatus status,
            LocalDateTime start,
            LocalDateTime end);


    List<Reminder> findByUser_IdAndReminderDateTimeGreaterThanEqualAndReminderDateTimeLessThanOrderByReminderDateTimeAsc(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Reminder> findByUser_IdAndStatusAndReminderDateTimeBetween(
            Long userId,
            ReminderStatus status,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Reminder> findByUser_IdAndStatusAndRepeatType(
            Long userId,
            ReminderStatus status,
            RepeatType repeatType
    );

    Optional<Reminder> findFirstByUser_IdAndStatusOrderByReminderDateTimeAsc(
            Long userId,
            ReminderStatus status
    );

    List<Reminder> findByUser_IdAndStatusAndReminderTextContainingIgnoreCase(
            Long userId,
            ReminderStatus status,
            String reminderText
    );
}