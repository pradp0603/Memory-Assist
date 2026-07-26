package com.demo.alzheimerassist.controller;

import com.demo.alzheimerassist.dto.ContactRequest;
import com.demo.alzheimerassist.dto.ContactResponse;
import com.demo.alzheimerassist.service.ContactService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService service;

    public ContactController(ContactService service){

        this.service = service;

    }

    @PostMapping
    public ContactResponse save(@RequestBody ContactRequest request){

        return service.save(request);

    }

    @GetMapping("/{userId}")

    public List<ContactResponse> getAll(@PathVariable Long userId){

        return service.getAll(userId);

    }

    @GetMapping("/{userId}/{name}")

    public ContactResponse get(@PathVariable Long userId, @PathVariable String name){

        return service.getByName(userId,name);

    }

    @PutMapping("/{id}")

    public ContactResponse update(@PathVariable Long id, @RequestBody ContactRequest request){

        return service.update(id,request);

    }

    @DeleteMapping("/{id}")

    public String delete(@PathVariable Long id){

        service.delete(id);

        return "Deleted Successfully";

    }

}