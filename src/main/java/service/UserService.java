package service;

import model.User;

/**
 * Service contract for user-related use cases.
 * Service layer orchestrates business rules and DAO calls.
 */
public interface UserService {

    void registerUser(User user);

    User getUserById(Long id);

    User getUserByUsername(String username);
}
