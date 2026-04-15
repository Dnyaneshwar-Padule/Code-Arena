package service;

import model.User;

/**
 * Service contract for user-related use cases.
 * Service layer orchestrates business rules and DAO calls.
 */
public interface UserService {

    User registerUser(String username, String email, String password);

    User loginUser(String email, String password);

    User getUserById(Long id);

    User getUserByEmail(String email);

    User getUserByUsername(String username);

    void resetPassword(String email, String newPassword);
    
    User createAdmin(String username, String email, String password);
}
