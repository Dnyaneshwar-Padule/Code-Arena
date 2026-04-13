package dao;

import model.User;

/**
 * DAO contract for user persistence operations.
 * DAO layer isolates Hibernate data access from service/controller layers.
 */
public interface UserDAO {

    void saveUser(User user);

    User findById(Long id);

    User findByEmail(String email);

    User findByUsername(String username);
}
