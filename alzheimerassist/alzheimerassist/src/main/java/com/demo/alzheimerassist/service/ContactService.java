package com.demo.alzheimerassist.service;

import com.demo.alzheimerassist.dto.ContactRequest;
import com.demo.alzheimerassist.dto.ContactResponse;

import java.util.List;

public interface ContactService {

    ContactResponse save(ContactRequest request);

    List<ContactResponse> getAll(Long userId);

    ContactResponse getByName(Long userId,
                              String name);

    ContactResponse update(Long id,
                           ContactRequest request);

    void delete(Long id);

}