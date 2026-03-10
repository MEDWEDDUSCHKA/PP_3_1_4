package boot_security.services;

import boot_security.models.User;
import java.util.Map;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    void saveUser(User user);
    void updateUser(User user);
    void deleteUser(Long id);
    User findByUsername(String username);

    User createUser(String firstName, String lastName, Integer age, String email, String password, List<Long> roleIds);
    void updateUserData(Long id, String firstName, String lastName, Integer age, String email, String password, List<Long> roleIds);

    User createUserFromMap(Map<String, Object> userData);
    User updateUserFromMap(Long id, Map<String, Object> userData);

}
