package dao;

import model.User;

/**
 * DAO contract for user persistence operations.
 * DAO layer isolates Hibernate data access from service/controller layers.
 */
public interface UserDAO {

    void save(User user);

    User findById(Long id);

    User findByUsername(String username);
}
