package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import src.service.AutoansweringService;

@RestController
public class LoginController {

	@Autowired
	private AutoansweringService service;

	@GetMapping(path = "/connect")
	public void get() {
		service.connect();
	}

	@PostMapping(path = "/post")
	public void p() {
		service.answer2();

	}
}
