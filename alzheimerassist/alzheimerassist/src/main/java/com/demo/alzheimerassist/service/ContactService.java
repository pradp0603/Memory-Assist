package com.demo.alzheimerassist.service;

import com.demo.alzheimerassist.dto.AIResponse;
import com.demo.alzheimerassist.dto.ContactRequest;
import com.demo.alzheimerassist.dto.ContactResponse;
import com.demo.alzheimerassist.entity.RelationshipType;

import java.util.List;

public interface ContactService {

    ContactResponse save(ContactRequest request);

    List<ContactResponse> getAll(Long userId);

    ContactResponse getByName(Long userId, String name);

    ContactResponse update(Long id, ContactRequest request);

    void delete(Long id);

    ContactResponse saveContact(ContactRequest request);

    ContactResponse getContactByRelationship(Long userId, RelationshipType relationship);

    ContactResponse getContactByName(Long userId, String contactName);

    void deleteContact(Long userId, String contactName, RelationshipType relationship);

    ContactResponse updateContact(ContactRequest request, AIResponse aiResponse);

}