package com.API.demo.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.API.demo.dao.ContactRepository;
import com.API.demo.dao.UserRepository;
import com.API.demo.entities.Contact;
import com.API.demo.entities.User;

@RestController
public class searchController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	@GetMapping("/search/{query}")
public  ResponseEntity<?> search(@PathVariable("query") String query,  Principal principal)
{
System.out.println(query);		
User user=this.userRepository.getUserByUserName(principal.getName());
List<Contact> contacts=  this.contactRepository.findByNameContainingAndUser(query, user);
return ResponseEntity.ok(contacts);
}
}
