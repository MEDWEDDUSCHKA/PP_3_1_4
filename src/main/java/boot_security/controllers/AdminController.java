package boot_security.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import boot_security.models.Role;
import boot_security.models.User;
import boot_security.services.RoleService;
import boot_security.services.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    private final UserService userService;
    private final RoleService roleService;
    
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/currentAdmin")
    public ResponseEntity<User> getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok((User) authentication.getPrincipal());
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
    
    @PostMapping("/create-user")
    public ResponseEntity<User> createUser(@RequestBody Map<String, Object> userData) {
        User newUser = userService.createUserFromMap(userData);
        return ResponseEntity.ok(newUser);
    }   
    
    @PutMapping("/update-user")
    public ResponseEntity<User> updateUser(@RequestBody Map<String, Object> userData) {
        User updatedUser = userService.updateUserFromMap(userData);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}