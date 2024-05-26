package com.API.demo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.API.demo.dao.ContactRepository;
import com.API.demo.dao.UserRepository;
import com.API.demo.entities.Contact;
import com.API.demo.entities.User;
import com.API.demo.helper.Message;



@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	@ModelAttribute
	
	
	public void commonData(Model model,Principal principal)
	{
		String userName=principal.getName();
		System.out.println("username "+userName);
		User user=userRepository.getUserByUserName(userName);
		System.out.println("user "+user);
		model.addAttribute("user", user);
		
	}
	
@RequestMapping("/index")
public String dashboard(Model model,Principal principal)
{
	
	return "user_dashboard";
}
@GetMapping("/add-contact")
public String addContactForm(Model model)
{
	model.addAttribute("title","Add Contact");
	model.addAttribute("contact",new Contact());
	return "add_contact";
}
@PostMapping("/process-contact")
public String processContact(@Valid @ModelAttribute("contact") Contact contact,BindingResult result,@RequestParam("profileImage") MultipartFile file,
		Principal principal,Model model,HttpSession session)
{
	try
	{
	String name=principal.getName();
	User user=this.userRepository.getUserByUserName(name);
	if(result.hasErrors())
	{
		System.out.println("ERROR "+result.toString());
		model.addAttribute("user", user);
	}
	if(file.isEmpty())
	{
		System.out.println("file is empty");
		contact.setImage("contact.png");
//		throw new Exception();
	}else
	{
		contact.setImage(file.getOriginalFilename());
		File savefile=new ClassPathResource("static/img").getFile();
		Path path=Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	}

	contact.setUser(user);
	user.getContacts().add(contact);
	this.userRepository.save(user);
	System.out.println("DATA "+contact);
	session.setAttribute("message",new Message("your contact is added","success"));
	}
	catch (Exception e) {
		System.out.println("message "+e.getMessage());
		e.printStackTrace();
		session.setAttribute("message",new Message("something went wrong ","danger"));
	}
	return "add_contact";
}




@GetMapping("/show-contacts/{page}")
public String showContacts(@PathVariable("page") Integer page ,Model model,Principal principal)
{
	model.addAttribute("title", "show user contacts");
	String userName=principal.getName();
	User user=this.userRepository.getUserByUserName(userName);
//	List<Contact> list= user.getContacts();
	Pageable pageable=PageRequest.of(page, 5);
	 Page<Contact> contacts=this.contactRepository.findContactByUser(user.getId(), pageable);
	 model.addAttribute("contacts", contacts);
	model.addAttribute("currentPage", page);
	model.addAttribute("totalPages", contacts.getTotalPages());
	return "show_contacts";
}
@RequestMapping("/{cid}/contact")
public String showContactDetail(@PathVariable("cid") Integer cid,Model model,Principal principal)
{

	System.out.println("cid "+cid );
	Optional<Contact> contactOptional=  this.contactRepository.findById(cid);
	Contact contact=contactOptional.get();
	String userName=principal.getName();
	User user=this.userRepository.getUserByUserName(userName);
	if(user.getId()==contact.getUser().getId())
	{
	model.addAttribute("contact", contact);	
	model.addAttribute("title", contact.getName());
	}
	return "contact_details";
}
@GetMapping("/delete/{cid}")
public String deleteContact(@PathVariable("cid") Integer cid,Model model,HttpSession session,Principal principal)
{
//	Optional<Contact> contactoptional= this.contactRepository.findById(cid);
//	Contact contact = contactoptional.get();
	Contact contact=this.contactRepository.findById(cid).get();
  User user=this.userRepository.getUserByUserName(principal.getName());
//	this.contactRepository.delete(contact);
  //contact.setImage("contact.png");
  try {
	File deleteFile=new ClassPathResource("static/img").getFile();
	File file1=new File(deleteFile,contact.getImage());
	 if(!contact.getImage().equals("contact.png"))
	  {
	file1.delete();
	  }
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	session.setAttribute("message",new Message("something went wrong during delete","danger"));
}
  user.getContacts().remove(contact);
  this.userRepository.save(user);
	session.setAttribute("message", new Message("contact deleted successfully...","success"));
	
	
	return "redirect:/user/show-contacts/0";
}
@PostMapping("/update-contact/{cid}")
public String updateContact(@PathVariable("cid") Integer cid , Model model)
{
	Optional<Contact> contactOptional=  this.contactRepository.findById(cid);
	Contact contact=contactOptional.get();
	model.addAttribute("contact", contact);
	model.addAttribute("title","update form - "+ contact.getName());
	model.addAttribute("title","Update Contact");
	return "update_form";
}
@PostMapping("/process-update")
public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Model model,HttpSession session,Principal principal)
{
	try
	{
		Contact oldcontactDetail=this.contactRepository.findById(contact.getCid()).get();
		if(!file.isEmpty())
		{
			//delete old photo
			File deleteFile=new ClassPathResource("static/img").getFile();
			File file1=new File(deleteFile,oldcontactDetail.getImage());
			file1.delete();
			
			
			//update photo
			File savefile=new ClassPathResource("static/img").getFile();
			Path path=Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		contact.setImage(file.getOriginalFilename());
		}
		else
		{
			contact.setImage(oldcontactDetail.getImage());
		}
User user =	this.userRepository.getUserByUserName(principal.getName());
contact.setUser(user);
this.contactRepository.save(contact);
session.setAttribute("message", new Message("your contact is updated...","success"));
	}
	catch (Exception e) {
	
	}
	System.out.println(("contact name "+contact.getName()));
	System.out.println("contact id "+contact.getCid());
	return "redirect:/user/"+contact.getCid()+"/contact";
}
@GetMapping("/profile")
public String yourProfile(Model model)
{
	model.addAttribute("title", "Profile Page");
	return "profile";
}
//@GetMapping("/profile-page")
//public String yourProfile()
//{
//	
//	return "profile";
//}
@GetMapping("/profileupdate")
public String profileupdate()
{
	
	return "update_profile";
}
@PostMapping("/process-updateprofile")
public String profileupdatee(@ModelAttribute User user,Principal principal,@RequestParam("profileImage") MultipartFile file)
{
	User oldcontactDetail=this.userRepository.findById(user.getId()).get();
	try
	{
	if(!file.isEmpty())
	{
		//delete old photo
		File deleteFile=new ClassPathResource("static/img").getFile();
		File file1=new File(deleteFile,oldcontactDetail.getImageUrl());
		file1.delete();
		
		
		//update photo
		File savefile=new ClassPathResource("static/img").getFile();
		Path path=Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	user.setImageUrl(file.getOriginalFilename());
	}
	else
	{
		//user.setImage(oldcontactDetail.getImageUrl());
		user.setImageUrl(oldcontactDetail.getImageUrl());
	}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	this.userRepository.save(oldcontactDetail);
////User user1 =	this.userRepository.getUserByUserName(principal.getName());
//	this.userRepository.
	return "profile";
}
}