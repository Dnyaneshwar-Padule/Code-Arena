package service.impl;

import dao.UserDAO;
import dao.impl.UserDAOImpl;
import exception.AuthenticationException;
import exception.DaoException;
import exception.ServiceException;
import exception.ValidationException;
import model.User;
import model.UserRole;
import service.UserService;
import util.HashUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of UserService.
 */
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class.getName());
    private final UserDAO userDAO;

    public UserServiceImpl() {
        this.userDAO = new UserDAOImpl();
    }

    @Override
    public User registerUser(String username, String email, String password) {
        try {
            String normalizedUsername = username == null ? "" : username.trim();
            String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
            String normalizedPassword = password == null ? "" : password.trim();

            if (normalizedUsername.isEmpty() || normalizedEmail.isEmpty() || normalizedPassword.isEmpty()) {
                throw new ValidationException("Please fill all required fields.");
            }
            if (!normalizedEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                throw new ValidationException("Please enter a valid email address.");
            }
            if (normalizedPassword.length() < 8) {
                throw new ValidationException("Password must be at least 8 characters.");
            }

            if (userDAO.findByEmail(normalizedEmail) != null || userDAO.findByUsername(normalizedUsername) != null) {
                throw new ValidationException("User already exists.");
            }

            String salt = HashUtil.generateSalt();
            String passwordHash = HashUtil.hashPassword(normalizedPassword, salt);

            User user = new User();
            user.setUsername(normalizedUsername);
            user.setEmail(normalizedEmail);
            user.setSalt(salt);
            user.setPasswordHash(passwordHash);

            userDAO.saveUser(user);
            return user;
        } catch (ValidationException ex) {
            throw ex;
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Register user failed at DAO layer", ex);
            throw new ServiceException("Unable to complete registration right now.", ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected register user error", ex);
            throw new ServiceException("Unable to complete registration right now.", ex);
        }
    }

    @Override
    public User loginUser(String email, String password) {
        try {
            String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
            String normalizedPassword = password == null ? "" : password;

            if (normalizedEmail.isEmpty() || normalizedPassword.isEmpty()) {
                throw new ValidationException("Please provide email and password.");
            }

            User existingUser = userDAO.findByEmail(normalizedEmail);
            if (existingUser == null) {
                throw new AuthenticationException("Invalid email or password.");
            }

            boolean isValidPassword = HashUtil.verifyPassword(
                    normalizedPassword,
                    existingUser.getPasswordHash(),
                    existingUser.getSalt()
            );

            if (!isValidPassword) {
                throw new AuthenticationException("Invalid email or password.");
            }

            return existingUser;
        } catch (ValidationException | AuthenticationException ex) {
            throw ex;
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Login user failed at DAO layer", ex);
            throw new ServiceException("Unable to process login right now. Please try again.", ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected login user error", ex);
            throw new ServiceException("Unable to process login right now. Please try again.", ex);
        }
    }

    @Override
    public User getUserById(Long id) {
        try {
            return userDAO.findById(id);
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Get user by id failed", ex);
            throw new ServiceException("Unable to load user details.", ex);
        }
    }

    @Override
    public User getUserByEmail(String email) {
        if (email == null) {
            return null;
        }
        try {
            return userDAO.findByEmail(email.trim().toLowerCase());
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Get user by email failed", ex);
            throw new ServiceException("Unable to load user details.", ex);
        }
    }

    @Override
    public User getUserByUsername(String username) {
        try {
            return userDAO.findByUsername(username);
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, "Get user by username failed", ex);
            throw new ServiceException("Unable to load user details.", ex);
        }
    }

	@Override
	public User createAdmin(String username, String email, String password) {
		User admin = new User();
	
        String salt = HashUtil.generateSalt();
        String passwordHash = HashUtil.hashPassword(password, salt);
		
		admin.setUsername(username);
		admin.setEmail(email);
		admin.setRole(UserRole.ADMIN);
		
        admin.setSalt(salt);
        admin.setPasswordHash(passwordHash);

        userDAO.saveUser(admin);
        return admin;
	}
}
