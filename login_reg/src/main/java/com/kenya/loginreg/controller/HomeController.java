package com.kenya.loginreg.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.kenya.loginreg.models.LoginUser;
import com.kenya.loginreg.models.User;
import com.kenya.loginreg.service.UserService;

@Controller
public class HomeController {

	// Add once service is implemented:
	@Autowired
	private UserService userService;

	// ============== REGISTER =================
	@GetMapping("/")
	public String index(Model model) {
		// Bind empty User and LoginUser objects to the JSP
		// to capture the form input
		model.addAttribute("newUser", new User());
		model.addAttribute("newLogin", new LoginUser());
		return "index.jsp";
	}

	// =============== Register Method ==============
	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("newUser") User newUser, BindingResult result, Model model,
			HttpSession session) {
		 userService.register(newUser, result);

		if (result.hasErrors()) {
			// send in the empty LoginUser before re-rendering the page.
			model.addAttribute("newLogin", new LoginUser());
			return "index.jsp";
		}else {
			
			session.setAttribute("user_Id", newUser.getId());
			return "redirect:/dashboard";
		}
	}

	// =================== DASHBOARD ====================
	@GetMapping("/dashboard")
	public String dashboard(HttpSession s, Model model) {
		Long userId = (Long) s.getAttribute("user_Id");
		
		if (userId == null) {
			return "redirect:/home";
		}else {
			User user = userService.findOne(userId);
			model.addAttribute("user", user);
			return "dashboard.jsp";
		}
	}

	
	// ================== LOGOUT ====================
	@GetMapping("/logout")
	public String logout(HttpSession session) {

		// Set userId to null and redirect to login/register page
		session.setAttribute("user_Id", null);

		return "redirect:/";
	}

	// ================ LOGIN =========================
	@PostMapping("/login")
	public String login(@Valid @ModelAttribute("newLogin") LoginUser newLogin, BindingResult result, Model model,
			HttpSession session) {

		User user = userService.login(newLogin, result);

		if (result.hasErrors()) {
			model.addAttribute("newUser", new User());
			return "index.jsp";
		}

		session.setAttribute("user_Id", user.getId());

		return "redirect:/dashboard";
	}

}
