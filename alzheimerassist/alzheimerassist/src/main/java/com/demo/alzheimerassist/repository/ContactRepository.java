package com.demo.alzheimerassist.repository;

import com.demo.alzheimerassist.entity.Contact;
import com.demo.alzheimerassist.entity.RelationshipType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findByUser_Id(Long userId);

    Optional<Contact> findByUser_IdAndNameIgnoreCase(Long userId, String name);

    Optional<Contact> findByUser_IdAndRelationship(Long userId, RelationshipType relationship);



}