package com.API.demo.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Entity
@Table(name="user_data")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
private int id;
	@NotBlank(message="Name field is required")
	@Size(min=2,max=20,message="min 2 and max 20 characters are allowed")
private String name;
@Column(unique = true)
@NotBlank(message = "email cannot be empty")
@Pattern (regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$" ,message="email must contain ^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")
private String email;
@NotBlank(message="Password cannot be empty")
//@Pattern(regexp="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\\\S+$).{8, 20}$" ,message="password must contain ^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\\\S+$).{8, 20}$" )
private String password;
private String role;
private boolean enabled;
private String imageUrl;
@Column(length=500)
@NotBlank(message="About field is required")
private String about;
@OneToMany(cascade = CascadeType.ALL,mappedBy = "user",orphanRemoval = true )
private List<Contact> contacts=new ArrayList<>();
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}
public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}
public String getRole() {
	return role;
}
public void setRole(String role) {
	this.role = role;
}
public boolean isEnabled() {
	return enabled;
}
public void setEnabled(boolean enabled) {
	this.enabled = enabled;
}
public String getImageUrl() {
	return imageUrl;
}
public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
}
public String getAbout() {
	return about;
}
public void setAbout(String about) {
	this.about = about;
}
public List<Contact> getContacts() {
	return contacts;
}
public void setContacts(List<Contact> contacts) {
	this.contacts = contacts;
}


}
