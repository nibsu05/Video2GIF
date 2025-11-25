package model.BO;

import model.DAO.UserDAO;
import model.Bean.User;
import utils.PasswordUtil;

public class UserBO {
	
	private UserDAO userDAO;
	
	public UserBO() {
		userDAO = new UserDAO();
	}

	public User login(String username, String password) {
		return userDAO.authenticateUser(username, password);
	}
	
	public User getUserByUsername(String username) {
		return userDAO.getUserByUsername(username);
	}
    
    public boolean registerNewUser(String username, String plainPassword, String email) {
        if (userDAO.isUsernameTaken(username)) {
            return false;
        }
        
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        
        User newUser = new User(username, hashedPassword, email);
        return userDAO.registerNewUser(newUser);
    }
}