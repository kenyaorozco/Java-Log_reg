package com.kenya.loginreg.service;

import java.util.List;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.kenya.loginreg.models.LoginUser;
import com.kenya.loginreg.models.User;
import com.kenya.loginreg.repo.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepo;
    
    // TO-DO: Write register and login methods!
    public User register(User newUser, BindingResult result) {
    	Optional<User> potentailUser = userRepo.findByEmail(newUser.getEmail());
		if (potentailUser.isPresent()) {
			result.rejectValue("email", "registerError", "this email is taken");
		}
		if (!newUser.getPassword().equals(newUser.getConfirm())) {
			result.rejectValue("confirm", "registerError", "passwords must match");
		}

//    	return the errors back
		if (result.hasErrors()) {
			return null;
		} else {
//    		has the password
			String hashed = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());
			newUser.setPassword(hashed);
//    		SAVE USER TO THE DB!!
			User newCreatedUser = userRepo.save(newUser);
			return newCreatedUser;
		}
    }
    
    
    public User login(LoginUser newLogin, BindingResult result) {
	Optional<User> potentialUser = userRepo.findByEmail(newLogin.getEmail());
        
    	// Find user in the DB by email
        // Reject if NOT present
    	if(!potentialUser.isPresent()) {
    		result.rejectValue("email", "Matches", "User not found!");
    		return null;
    	}
    	
    	// User exists, retrieve user from DB
    	User user = potentialUser.get();
        
        // Reject if BCrypt password match fails
    	if(!BCrypt.checkpw(newLogin.getPassword(), user.getPassword())) {
    	    result.rejectValue("password", "Matches", "Invalid Password!");
    	}
    	
    	// Return null if result has errors
    	if(result.hasErrors()) {
    		return null;
    	}
    	
        // Otherwise, return the user object
        return user;
    }
    
    
    // ============= Read all users ==============
    public List<User> showAllUsers() {
    	return userRepo.findAll();    	
    }
    
    
    // =========== Read One User ==================
    public User findOne(Long id) {
    	Optional<User> optionalUser = userRepo.findById(id);
    	if(optionalUser.isPresent()) {
    		return optionalUser.get();
    	}
    		return null;
    }
    
    
}