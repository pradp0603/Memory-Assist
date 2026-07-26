package com.demo.alzheimerassist.service.impl;

import com.demo.alzheimerassist.dto.ContactRequest;
import com.demo.alzheimerassist.dto.ContactResponse;
import com.demo.alzheimerassist.entity.Contact;
import com.demo.alzheimerassist.entity.User;
import com.demo.alzheimerassist.exception.ResourceNotFoundException;
import com.demo.alzheimerassist.repository.ContactRepository;
import com.demo.alzheimerassist.repository.UserRepository;
import com.demo.alzheimerassist.service.ContactService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository repository;

    private final UserRepository userRepository;

    public ContactServiceImpl(ContactRepository repository,
                              UserRepository userRepository){

        this.repository = repository;
        this.userRepository = userRepository;

    }

    @Override
    public ContactResponse save(ContactRequest request){

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Contact contact = new Contact();

        contact.setUser(user);
        contact.setName(request.getName());
        contact.setRelationship(request.getRelationship());
        contact.setPhoneNumber(request.getPhoneNumber());
        contact.setEmail(request.getEmail());
        contact.setEmergencyContact(request.isEmergencyContact());

        Contact saved = repository.save(contact);

        return convert(saved);

    }



    @Override
    public List<ContactResponse> getAll(Long userId){

        return repository.findByUser_Id(userId)
                .stream()
                .map(this::convert)
                .toList();
    }

    @Override
    public ContactResponse getByName(Long userId,
                                     String name){

        Contact contact = repository.findByUser_IdAndNameIgnoreCase(userId,name)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        return convert(contact);

    }

    @Override
    public ContactResponse update(Long id, ContactRequest request){

        Contact contact = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        contact.setName(request.getName());
        contact.setRelationship(request.getRelationship());
        contact.setPhoneNumber(request.getPhoneNumber());
        contact.setEmail(request.getEmail());
        contact.setEmergencyContact(request.isEmergencyContact());

        return convert(repository.save(contact));

    }

    @Override
    public void delete(Long id){

        repository.deleteById(id);

    }

    private ContactResponse convert(Contact contact){

        ContactResponse response = new ContactResponse();

        response.setId(contact.getId());
        response.setName(contact.getName());
        response.setRelationship(contact.getRelationship());
        response.setPhoneNumber(contact.getPhoneNumber());
        response.setEmail(contact.getEmail());
        response.setEmergencyContact(contact.isEmergencyContact());

        return response;

    }
}
