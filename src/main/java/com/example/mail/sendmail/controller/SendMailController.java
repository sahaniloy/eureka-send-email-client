package com.example.mail.sendmail.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class SendMailController {
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private JavaMailSender mailSender;

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("tsmtp.nts.net");
		mailSender.setPort(25);

		return mailSender;
	}

	@RequestMapping(path = "/email/trigger", method = RequestMethod.POST)
	public String triggerEmail() {

		String response = restTemplate.exchange("http://student-service/getStudentDetailsForSchool/{schoolname}",
				HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
				}, "abcschool").getBody();

		SimpleMailMessage message = new SimpleMailMessage();
		message.setText(response);
		message.setTo("niloy.saha@rssoftware.com");
		message.setFrom("niloy.saha@rssoftware.com");
		message.setSubject("Test subject");
		
		try {
			
			mailSender.send(message);
			return "{\"message\": \"OK\"}";
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"message\": \"Error\"}";
		}
	}
}
