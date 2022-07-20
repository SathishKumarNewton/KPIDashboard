
package com.prodian.rsgirms.userapp.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.prodian.rsgirms.dashboard.model.Dashboard;
import com.prodian.rsgirms.dashboard.service.impl.DashboardServiceImpl;
import com.prodian.rsgirms.userapp.model.User;
import com.prodian.rsgirms.userapp.service.UserService;

/**
 * @author CSS
 *
 */

@Controller
public class LoginController {

	@Autowired
	private UserService userService;

	@Autowired
	private DashboardServiceImpl dashboardService;

//	@GetMapping(value = { "/", "/login" })
	public ModelAndView login() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return modelAndView;
	}
	
	@GetMapping(value = { "/", "/login" })
	public ModelAndView signin() {
		ModelAndView model = new ModelAndView("login");
		return model;
	}

	@GetMapping(value = "/registration")
	public ModelAndView registration() {
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("registration");
		return modelAndView;
	}

	@PostMapping(value = "/registration")
	public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView();
		User userExists = userService.findUserByUserName(user.getUserName());
		if (userExists != null) {
			bindingResult.rejectValue("userName", "error.user",
					"There is already a user registered with the user name provided");
		}
		if (bindingResult.hasErrors()) {
			modelAndView.setViewName("registration");
		} else {
			userService.saveUser(user, false);
			modelAndView.addObject("successMessage", "User has been registered successfully");
			modelAndView.addObject("user", new User());
			modelAndView.setViewName("registration");

		}
		return modelAndView;
	}

	@GetMapping(value = "/admin/old_home")
	public ModelAndView oldhome() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		modelAndView.addObject("userName", user.getName() + " " + user.getLastName());
		modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
		modelAndView.addObject("userList", userService.getAllUsers());
		modelAndView.addObject("dashboardList", dashboardService.getAllDashboards());
		modelAndView.addObject("dashboard", new Dashboard());
		modelAndView.addObject("user", new User());
		modelAndView.setViewName("admin/welcomeHome");
		return modelAndView;
	}

	@PostMapping("/admin/deleteUser")
	public String deleteUser(@RequestBody List<User> users) {
		userService.deleteUser(users);
		return "redirect:/admin/home";
	}

	@PostMapping("/admin/editUser")
	public String editUser(@ModelAttribute User user) {
		userService.editUser(user);
		return "redirect:/admin/home";
	}

	@PostMapping("/admin/saveUser")
	public ModelAndView saveUser(@Valid User user, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView("admin/userMaster");
		User userExists = userService.findUserByUserName(user.getUserName());
		if (userExists != null) {
			bindingResult.rejectValue("userName", "error.user",
					"There is already a user registered with the user name provided");
		}

		if (!bindingResult.hasErrors()) {
			userService.saveUser(user, true);
			modelAndView.addObject("successMessage", "User added Successfully");

		}
		modelAndView.addObject("user", new User());
		modelAndView.addObject("userList", userService.getAllUsers());
		return modelAndView;

	}

	@PostMapping("/admin/addUser")
	public String addUser(@Valid User user) {
		userService.saveUser(user, true);
		return "redirect:/admin/home";
	}

	@PostMapping("/admin/checkUserExist")
	public @ResponseBody Boolean checkUserExist(String userName) {
		if (userService.findUserByUserName(userName) == null) {
			return true;
		} else {
			return false;
		}
	}

	@GetMapping("/admin/getUserById")
	public @ResponseBody User getUserById(@RequestParam Integer userId) {
		return userService.getUserById(userId);
	}

	@PostMapping("/admin/checkUsernameExistByUser")
	public @ResponseBody Boolean checkUsernameExistByUser(String userName, Integer id) {
		User user = userService.findUserByUserName(userName);
		if (user == null || (user != null && user.getId().intValue() == id.intValue())) {
			return true;
		} else {
			return false;
		}
	}
	
	@GetMapping("/admin/home")
	public ModelAndView home() {
		ModelAndView modelAndView = new ModelAndView("admin/adminHome");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		modelAndView.addObject("userName", user.getName() + " " + user.getLastName());
		modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
		modelAndView.addObject("userList", userService.getAllUsers());
		modelAndView.addObject("dashboardList", dashboardService.getAllDashboards());
		modelAndView.addObject("dashboard", new Dashboard());
		modelAndView.addObject("user", new User());
		return modelAndView;
	}

}
