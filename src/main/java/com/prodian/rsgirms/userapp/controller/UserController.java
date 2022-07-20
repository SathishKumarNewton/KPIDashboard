package com.prodian.rsgirms.userapp.controller;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.prodian.rsgirms.dashboard.service.impl.DashboardServiceImpl;
import com.prodian.rsgirms.userapp.model.User;
import com.prodian.rsgirms.userapp.service.UserService;

@Controller
public class UserController {

	@Autowired
	private DashboardServiceImpl dashboardService;
	
	@Autowired
	private UserService userService;

	@GetMapping("/user/oldhome")
	public ModelAndView olduserHome() {
		ModelAndView model = new ModelAndView("oldUserHome");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		model.addObject("userDashboardList", dashboardService.getUserDashboardListResponseByUserId(user.getId()));
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		Integer currentHour = cal.get(Calendar.HOUR);
		
		model.addObject("welcomeText",currentHour+" "+user.getName() + " " + user.getLastName());
		return model;
	}
	
	@GetMapping("/user/home")
	public ModelAndView userHome() {
		ModelAndView model = new ModelAndView("UserHome");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("auth.getName()-->"+auth.getName());
		User user = userService.findUserByUserName(auth.getName());
		//User user = userService.findUserByUserName("shyam");
		model.addObject("userDashboardList", dashboardService.getUserDashboardListResponseByUserId(user.getId()));
		//model.addObject("userDashboardList", dashboardService.getUserDashboardListResponseByUserId(101));
		model.addObject("userName",user.getName() + " " + user.getLastName());
		model.addObject("currentDate", DateFormat.getDateInstance().format(new Date()));
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.setTimeZone(TimeZone.getTimeZone("IST"));
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		String welcomeText = "";
		if(hour >= 0 && hour<12) {
			welcomeText+="Good Morning. "+user.getName() + " " + user.getLastName()+"!";
		}else if(hour>=12 && hour<16) {
			welcomeText="Good Afternoon. "+user.getName() + " " + user.getLastName()+"!";
		}else {
			welcomeText="Good Evening. "+user.getName() + " " + user.getLastName()+"!";
		}
		model.addObject("welcomeText", welcomeText);
		return model;
	}

}
