package boot_security.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import boot_security.models.User;
import java.util.Map;
import boot_security.repositories.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    @Override
    @Transactional
    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    @Override
    @Transactional
    public User createUserFromMap(Map<String, Object> userData) {
        String firstName = (String) userData.get("firstName");
        String lastName = (String) userData.get("lastName");
        String email = (String) userData.get("email");
        String password = (String) userData.get("password");
    
        Object ageObj = userData.get("age");
        Integer age = ageObj != null ? (ageObj instanceof Integer ? (Integer) ageObj : Integer.parseInt(ageObj.toString())) : null;
    
        @SuppressWarnings("unchecked")
        List<Object> roleIdsObj = (List<Object>) userData.get("roleIds");
        List<Long> roleIds = new java.util.ArrayList<>();
        for (Object id : roleIdsObj) {
        roleIds.add(id instanceof Integer ? ((Integer) id).longValue() : (Long) id);
        }
        
        User user = new User();
        user.setUsername(firstName);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roleService.getRolesByIds(roleIds));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUserFromMap(Map<String, Object> userData) {
        Long id = ((Number) userData.get("id")).longValue();
        String firstName = (String) userData.get("firstName");
        String lastName = (String) userData.get("lastName");
        String email = (String) userData.get("email");
        String password = (String) userData.get("password");
    
        Object ageObj = userData.get("age");
        Integer age = ageObj != null ? (ageObj instanceof Integer ? (Integer) ageObj : Integer.parseInt(ageObj.toString())) : null;
    
        @SuppressWarnings("unchecked")
        List<Object> roleIdsObj = (List<Object>) userData.get("roleIds");
        List<Long> roleIds = new java.util.ArrayList<>();
        for (Object roleId : roleIdsObj) {
        roleIds.add(roleId instanceof Integer ? ((Integer) roleId).longValue() : (Long) roleId);
        }

                User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setUsername(firstName);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAge(age);
            user.setEmail(email);
        
            if (password != null && !password.trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(password));
            }
        
            user.setRoles(roleService.getRolesByIds(roleIds));
            userRepository.save(user);
        }
        return getUserById(id);
    }
}

