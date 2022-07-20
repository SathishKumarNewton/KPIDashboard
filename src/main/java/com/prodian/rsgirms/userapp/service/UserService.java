
package com.prodian.rsgirms.userapp.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.prodian.rsgirms.userapp.model.Role;
import com.prodian.rsgirms.userapp.model.User;
import com.prodian.rsgirms.userapp.repository.RoleRepository;
import com.prodian.rsgirms.userapp.repository.UserRepository;

/**
 * @author CSS
 *
 */

@Service
public class UserService {

	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	public UserService(UserRepository userRepository, RoleRepository roleRepository,
			BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public User findUserByUserName(String userName) {
		return userRepository.findByUserName(userName);
	}

	public User saveUser(User user, Boolean isAdmin) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setActive(true);		
		if(isAdmin) {
			Role userRole = roleRepository.findByRole("ADMIN");
			user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		}else {
			Role userRole = roleRepository.findByRole("USER");
			user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		}
		return userRepository.save(user);
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public void deleteUser(List<User> users) {
		for (User user : users) {
			User userFromDb = userRepository.findByUserName(user.getUserName());
			userFromDb.setActive(false);
			userRepository.save(userFromDb);

		}
	}

	public void editUser(User user) {
		User userFromDb = userRepository.findById(user.getId());
		userFromDb.setUserName(user.getUserName());
		userFromDb.setName(user.getName());
		userFromDb.setLastName(user.getLastName());
		userFromDb.setEmail(user.getEmail());
		userFromDb.setActive(user.getActive());
		userRepository.save(userFromDb);

	}

	public User getUserById(Integer userId) {
		return userRepository.findById(userId);
	}

}
