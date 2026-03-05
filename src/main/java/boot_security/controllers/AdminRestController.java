package boot_security.controllers;

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
        
            User newUser = userService.createUser(firstName, lastName, age, email, password, roleIds);
            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> userData) {
        try {
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
        
            userService.updateUserData(id, firstName, lastName, age, email, password, roleIds);
            User updatedUser = userService.getUserById(id);
            return ResponseEntity.ok(updatedUser);
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
