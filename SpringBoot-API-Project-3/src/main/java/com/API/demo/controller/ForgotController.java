
package com.API.demo.controller;


import java.util.Random;

import javax.mail.Session;
import javax.servlet.http.HttpSession;

import org.hibernate.internal.build.AllowSysOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.API.demo.dao.UserRepository;
import com.API.demo.entities.User;
import com.API.demo.service.EmailService;

@Controller
public class ForgotController {
	Random random= new Random(1000);
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@RequestMapping("/forgot")
public String openEmailForm(Model model)
{
		model.addAttribute("title","Forgot Password");
	return "forgot_email_form";
	
}
	
	
	@PostMapping("/send-otp")
public String sendotp(@RequestParam("email") String email,HttpSession session,Model model)
{
		System.out.println("email "+email);
		model.addAttribute("title","Send OTP");
		int otp=(int)(Math.random()*9000)+1000;
		System.out.println("OTP "+otp);
		
	String subject="OTP from AYUSH PVT. LTD.";
	String message=""
			+"<div style='border:1px solid #e2e2e2;padding:20px'>"
			+"<h1>"
			+"OTP :"
			+"<b>"+otp
			+"</n>"
			+"</h1>"
			+"</div>";
	String to=email;
			boolean flag= this.emailService.sendEmail(message, subject, to);
			System.out.println(flag);
			if(flag)
			{	
				session.setAttribute("myotp", otp);
				session.setAttribute("email",email);
				System.out.println("email is   "+email);
				System.out.println("otp is   "+otp);
				return "verify_otp";
			}
			else
			{
				
				session.setAttribute("message", "check your email id");
				return "forgot_email_form";
			}
	
	
}
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp,HttpSession session,Model model) 
	{
		model.addAttribute("title","Verify OTP");
Integer myOtp=(Integer)session.getAttribute("myotp");
System.out.println(" user otp"+otp);
	System.out.println(" our otp"+myOtp);
	String email=(String)session.getAttribute("email");
	System.out.println("emailll "+email);
	if(myOtp==otp)
	{
		User user=this.userRepository.getUserByUserName(email);
		if(user==null)
		{
			session.setAttribute("message","User does not exist !!");
		return "forgot_email_form";
		}
		else {
			
		}
		return "password_change_form";
	}
	else
	{
		session.setAttribute("message","you have entered wrong otp");
		return "verify_otp";
	}
	}
@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword,@RequestParam("newconfirmpassword") String newconfirmpassword,HttpSession session)
	{
		String email=(String)session.getAttribute("email");
		User user=this.userRepository.getUserByUserName(email);
		if(newpassword.equals(newconfirmpassword))
		{
		user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
		this.userRepository.save(user);
		return "redirect:/signin?change=password changed successfully";
		}
		else
		{
			session.setAttribute("message","password mismatch , please enter same password in both field");
			return "password_change_form";
		}
	}
}
