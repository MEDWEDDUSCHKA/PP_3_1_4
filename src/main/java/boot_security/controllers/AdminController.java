package boot_security.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import boot_security.models.Role;
import boot_security.models.User;
import boot_security.services.RoleService;
import boot_security.services.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    private final UserService userService;
    private final RoleService roleService;
    
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public ModelAndView adminPage(Principal principal) {
        ModelAndView mav = new ModelAndView("admin/users");
        mav.addObject("users", userService.getAllUsers());
        mav.addObject("allRoles", roleService.getAllRoles());
        
        User currentUser = userService.findByUsername(principal.getName());
        mav.addObject("currentUser", currentUser);
        return mav;
    }

    @GetMapping("/api/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/api/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    @GetMapping("/api/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
    
    @PostMapping("/api/users")
    public ResponseEntity<User> createUser(@RequestBody Map<String, Object> userData) {
        try {
            User newUser = userService.createUserFromMap(userData);
            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/api/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> userData) {
        try {
            User updatedUser = userService.updateUserFromMap(id, userData);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
