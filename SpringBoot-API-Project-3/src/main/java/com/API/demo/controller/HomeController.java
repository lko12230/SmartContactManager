package com.API.demo.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.API.demo.dao.UserRepository;
import com.API.demo.entities.User;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;
	@GetMapping("/")
	public String home(Model model)
	{
		System.out.println("hi");
		model.addAttribute("title","Home-Smart contact Manager");
		return "home";
	}
	@GetMapping("/about")
	public String about(Model model)
	{
		System.out.println("hi");
		model.addAttribute("title","About-Smart contact Manager");
		return "about";
	}
	
	@GetMapping("/signup")
	public String signup(Model model)
	{
		System.out.println("hi");
		model.addAttribute("title","SignUp-Smart contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	@PostMapping("/do-register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result ,@RequestParam(value="agreement",defaultValue = "false")boolean agreement,Model model,@RequestParam("profileImage") MultipartFile  file,HttpSession session)
	
	{
		
	try
	{
		if(!agreement)
		{
			System.out.println("you have not agreed terms and conditions");
		throw new Exception("you have not agreed terms and conditions");
		}
		if(result.hasErrors())
		{
			System.out.println("ERROR "+result.toString());
			model.addAttribute("user", user);
		}
		if(file.isEmpty())
		{
			System.out.println("file is empty");
			user.setImageUrl("default1.jpg");
//			throw new Exception();
		}
		else
		{
			user.setImageUrl(file.getOriginalFilename());
			File savefile=new ClassPathResource("static/img").getFile();
			Path path=Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			System.out.println(file.getOriginalFilename());
		
		}
	user.setRole("ROLE_USER");
	user.setPassword(passwordEncoder.encode(user.getPassword()));
	user.setEnabled(true);
	
		System.out.println("agreement "+agreement);
		System.out.println("user "+user);
		User result1= this.userRepository.save(user);
		System.out.println(result1);
		   model.addAttribute("user",new User());
		   session.setAttribute("message",new com.API.demo.helper.Message("Successfully Registered", "alert-success"));
	return "signup";
	}
	catch (Exception e) {
	e.printStackTrace();
	model.addAttribute("user", user);
	session.setAttribute("message", new com.API.demo.helper.Message("Something went wrong !!"+e.getMessage(), "alert-danger"));
	}
	return "signup";
	}
@GetMapping("/signin")
	public String customlogin(Model model)
	{
	model.addAttribute("title", "SignUp-Smart contact Manager");
		return "login";
	}
}

