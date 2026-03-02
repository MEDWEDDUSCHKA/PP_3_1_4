package boot_security.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import boot_security.models.User;
import boot_security.services.RoleService;
import boot_security.services.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    
    private final UserService userService;
    private final RoleService roleService;
    
    @Autowired
    public AdminRestController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody Map<String, Object> userData) {
        try {
            User user = new User();
            user.setFirstName((String) userData.get("firstName"));
            user.setLastName((String) userData.get("lastName"));
            
            // Handle age conversion
            Object ageObj = userData.get("age");
            if (ageObj != null) {
                user.setAge(ageObj instanceof Integer ? (Integer) ageObj : Integer.parseInt(ageObj.toString()));
            }
            
            user.setEmail((String) userData.get("email"));
            user.setPassword((String) userData.get("password"));
            user.setUsername((String) userData.get("firstName"));
            
            // Handle roleIds conversion
            @SuppressWarnings("unchecked")
            List<Object> roleIdsObj = (List<Object>) userData.get("roleIds");
            List<Long> roleIds = new java.util.ArrayList<>();
            for (Object id : roleIdsObj) {
                roleIds.add(id instanceof Integer ? ((Integer) id).longValue() : (Long) id);
            }
            user.setRoles(roleService.getRolesByIds(roleIds));
            
            userService.saveUser(user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> userData) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            user.setFirstName((String) userData.get("firstName"));
            user.setLastName((String) userData.get("lastName"));
            
            // Handle age conversion
            Object ageObj = userData.get("age");
            if (ageObj != null) {
                user.setAge(ageObj instanceof Integer ? (Integer) ageObj : Integer.parseInt(ageObj.toString()));
            }
            
            user.setEmail((String) userData.get("email"));
            user.setUsername((String) userData.get("firstName"));
            
            String password = (String) userData.get("password");
            if (password != null && !password.trim().isEmpty()) {
                user.setPassword(password);
            }
            
            // Handle roleIds conversion
            @SuppressWarnings("unchecked")
            List<Object> roleIdsObj = (List<Object>) userData.get("roleIds");
            List<Long> roleIds = new java.util.ArrayList<>();
            for (Object roleId : roleIdsObj) {
                roleIds.add(roleId instanceof Integer ? ((Integer) roleId).longValue() : (Long) roleId);
            }
            user.setRoles(roleService.getRolesByIds(roleIds));
            
            userService.updateUser(user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
