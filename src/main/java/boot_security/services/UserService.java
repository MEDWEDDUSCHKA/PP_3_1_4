package boot_security.services;

import boot_security.dto.UserDTO;
import boot_security.models.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    void deleteUser(Long id);
    User findByEmail(String email);

    User createUser(UserDTO userDTO);
    User updateUser(UserDTO userDTO);

}
