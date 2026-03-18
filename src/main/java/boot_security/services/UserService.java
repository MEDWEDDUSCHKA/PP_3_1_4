package boot_security.services;

import boot_security.models.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    void deleteUser(Long id);
    User findByEmail(String email);

    User createUser(String firstName, String lastName, String email, String password, Integer age, List<Long> roleIds);
    User updateUser(Long id, String firstName, String lastName, String email, String password, Integer age, List<Long> roleIds);

}
