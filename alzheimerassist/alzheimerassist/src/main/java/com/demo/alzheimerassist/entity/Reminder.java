package com.demo.alzheimerassist.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name="reminders")
@Data
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    private String title;

    @Column(length = 1000)
    private String description;

    private LocalDateTime reminderDateTime;

    @Enumerated(EnumType.STRING)
    private RepeatType repeatType;

    @Enumerated(EnumType.STRING)
    private ReminderStatus status;

    private LocalDateTime createdDate;

    @PrePersist
    public void onCreate(){

        createdDate = LocalDateTime.now();

        status = ReminderStatus.PENDING;

    }

    public Reminder() {
    }

}