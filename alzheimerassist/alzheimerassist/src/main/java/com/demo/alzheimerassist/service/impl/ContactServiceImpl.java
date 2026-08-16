package com.demo.alzheimerassist.service.impl;

import com.demo.alzheimerassist.dto.AIResponse;
import com.demo.alzheimerassist.dto.ContactRequest;
import com.demo.alzheimerassist.dto.ContactResponse;
import com.demo.alzheimerassist.entity.Contact;
import com.demo.alzheimerassist.entity.RelationshipType;
import com.demo.alzheimerassist.entity.User;
import com.demo.alzheimerassist.exception.MissingContactNameException;
import com.demo.alzheimerassist.exception.ResourceNotFoundException;
import com.demo.alzheimerassist.repository.ContactRepository;
import com.demo.alzheimerassist.repository.UserRepository;
import com.demo.alzheimerassist.service.ContactService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    private final UserRepository userRepository;

    public ContactServiceImpl(ContactRepository contactRepository,
                              UserRepository userRepository){

        this.contactRepository = contactRepository;
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

        Contact saved = contactRepository.save(contact);

        return convert(saved);

    }



    @Override
    public List<ContactResponse> getAll(Long userId){

        return contactRepository.findByUser_Id(userId)
                .stream()
                .map(this::convert)
                .toList();
    }

    @Override
    public ContactResponse getByName(Long userId,
                                     String name){

        Contact contact = contactRepository.findByUser_IdAndNameIgnoreCase(userId,name)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        return convert(contact);

    }

    @Override
    public ContactResponse update(Long id, ContactRequest request){

        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        contact.setName(request.getName());
        contact.setRelationship(request.getRelationship());
        contact.setPhoneNumber(request.getPhoneNumber());
        contact.setEmail(request.getEmail());
        contact.setEmergencyContact(request.isEmergencyContact());

        return convert(contactRepository.save(contact));

    }

    @Override
    public void delete(Long id){

        contactRepository.deleteById(id);

    }

    @Override
    public ContactResponse saveContact(ContactRequest request) {

        Contact contact = null;



        // If relationship is provided, search by relationship
        if (request.getRelationship() != null) {
/**
 * TODO :
 * The code needs to be refactored for if the patient has eg 2 sons and 1 son's names is already saved and want to
 * save 2nd son's contact it wont let you do that as it will return multiple records and we are not handling as a list here.
 * 2. ideally it should save more than one same relationship. eg 2 sons, 2 daughters
 */
            contact = contactRepository
                    .findByUser_IdAndRelationship(
                            request.getUserId(),
                            request.getRelationship())
                    .orElse(null);

        }
        // Otherwise search by contact name
        else if (request.getName() != null
                && !request.getName().isBlank()) {

            contact = contactRepository
                    .findByUser_IdAndNameIgnoreCase(
                            request.getUserId(),
                            request.getName())
                    .orElse(null);
        }

        // Create new contact if not found
        if (contact == null) {

            contact = new Contact();

            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("User not found"));

            contact.setUser(user);
            contact.setCreatedDate(LocalDateTime.now());

        }

        contact.setName(request.getName());
        contact.setPhoneNumber(request.getPhoneNumber());
        contact.setRelationship(request.getRelationship());

        Contact saved = contactRepository.save(contact);

        return convert(saved);
    }

    @Override
    public ContactResponse getContactByRelationship(Long userId, RelationshipType relationship) {

        Contact contact =contactRepository.findByUser_IdAndRelationship(
                                userId,
                                relationship)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Contact not found"));

        return convert(contact);
    }

    @Override
    public ContactResponse getContactByName(Long userId, String contactName) {

        Contact contact = contactRepository.findByUser_IdAndNameIgnoreCase(
                                userId,
                                contactName)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Contact not found"));

        return convert(contact);
    }

    @Override
    public void deleteContact(Long userId, String contactName, RelationshipType relationship) {

        Contact contact;

        if (relationship != null) {

            contact = contactRepository
                    .findByUser_IdAndRelationship(userId, relationship)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Contact not found"));

        } else {

            contact = contactRepository
                    .findByUser_IdAndNameIgnoreCase(userId, contactName)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Contact not found"));
        }

        contactRepository.delete(contact);
    }

    @Override
    public ContactResponse updateContact(ContactRequest request, AIResponse aiResponse) {

        Contact contact;

        if (aiResponse.getContactName() == null || aiResponse.getContactName().isBlank()) {
            throw new MissingContactNameException("Please provide the person's name along with the phone number so I can save the contact.");
        }

        // Search by relationship first
        if (aiResponse.getRelationship() != null) {

            contact = contactRepository
                    .findByUser_IdAndRelationship(
                            request.getUserId(),
                            aiResponse.getRelationship())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Contact not found"));
        }
        // Otherwise search by contact name
        else if (aiResponse.getContactName() != null &&
                !aiResponse.getContactName().isBlank()) {

            contact = contactRepository
                    .findByUser_IdAndNameIgnoreCase(
                            request.getUserId(),
                            aiResponse.getContactName())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Contact not found"));
        }
        else {
            throw new IllegalArgumentException("Relationship or Contact Name is required.");
        }

        // Update phone number if provided
        if (aiResponse.getPhoneNumber() != null &&
                !aiResponse.getPhoneNumber().isBlank()) {

            contact.setPhoneNumber(aiResponse.getPhoneNumber());
        }

        // Update name if provided
        if (aiResponse.getContactName() != null &&
                !aiResponse.getContactName().isBlank()) {

            contact.setName(aiResponse.getContactName());
        }

        // Update relationship if provided
        if (aiResponse.getRelationship() != null) {

            contact.setRelationship(aiResponse.getRelationship());
        }



        Contact updatedContact = contactRepository.save(contact);

        return convert(updatedContact);

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
