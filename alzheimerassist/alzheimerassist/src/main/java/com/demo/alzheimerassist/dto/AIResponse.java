package com.demo.alzheimerassist.dto;

import java.time.LocalDateTime;

import com.demo.alzheimerassist.entity.IntentType;
import com.demo.alzheimerassist.entity.MemoryType;
import com.demo.alzheimerassist.entity.RelationshipType;
import com.demo.alzheimerassist.entity.RepeatType;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AIResponse {

    @JsonSetter(nulls = Nulls.SKIP)
    private IntentType intent;

    @JsonSetter(nulls = Nulls.SKIP)
    private MemoryType memoryType;

    private String title;

    private String value;

    private String contactName;

    private String phoneNumber;

    @JsonSetter(nulls = Nulls.SKIP)
    private RelationshipType relationship;

    private LocalDateTime reminderDateTime;

    private String reminderText;

    public String getRelationshipName() {
        return relationshipName;
    }

    public void setRelationshipName(String relationshipName) {
        this.relationshipName = relationshipName;
    }

    private String relationshipName;

    public RepeatType getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(RepeatType repeatType) {
        this.repeatType = repeatType;
    }

    public String getReminderText() {
        return reminderText;
    }

    public void setReminderText(String reminderText) {
        this.reminderText = reminderText;
    }

    public LocalDateTime getReminderDateTime() {
        return reminderDateTime;
    }

    public void setReminderDateTime(LocalDateTime reminderDateTime) {
        this.reminderDateTime = reminderDateTime;
    }

    public RelationshipType getRelationship() {
        return relationship;
    }

    public void setRelationship(RelationshipType relationship) {
        this.relationship = relationship;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MemoryType getMemoryType() {
        return memoryType;
    }

    public void setMemoryType(MemoryType memoryType) {
        this.memoryType = memoryType;
    }

    public IntentType getIntent() {
        return intent;
    }

    public void setIntent(IntentType intent) {
        this.intent = intent;
    }

    @JsonSetter(nulls = Nulls.SKIP)
    private RepeatType repeatType;


}