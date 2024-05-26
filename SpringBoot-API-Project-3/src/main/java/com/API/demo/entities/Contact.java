package com.API.demo.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
@Data
@Entity
@Table(name="contact")
public class Contact {
	@Id 
	@GeneratedValue(strategy = GenerationType.AUTO)
private int cid;
@NotBlank(message="Contact name field is required")
@Size(min=2,max=20,message="min 2 and max 20 characters are allowed")
private String name;
@NotBlank(message="Contact second name field is required")
@Size(min=2,max=20,message="min 2 and max 20 characters are allowed")
private String secondName;
@NotBlank(message="Work field is required")
private String work;
@NotBlank(message = "email cannot be empty")
@Pattern (regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$" ,message="email must contain ^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")
private String email;
@NotBlank(message = "phone cannot be empty/phone number should be 10 digits characters are allowed")
private String phone;
private String image;
@Column(length=500)
@NotBlank(message = "description cannot be empty")
private String description;
@ManyToOne
@JsonIgnore
private User user;

}
