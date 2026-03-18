package boot_security.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import boot_security.dto.UserDTO;
import boot_security.models.Role;
import boot_security.models.User;
import boot_security.services.RoleService;
import boot_security.services.UserService;
import java.util.List;

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
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
    User user = userService.createUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), userDTO.getPassword(), userDTO.getAge(), userDTO.getRoleIds());
    return new ResponseEntity<>(user, HttpStatus.CREATED);
    }   
    
    @PutMapping("/update-user")
    public ResponseEntity<User> updateUser(@RequestBody UserDTO userDTO) {
    User user = userService.updateUser(userDTO.getId(), userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), userDTO.getPassword(), userDTO.getAge(), userDTO.getRoleIds());
    return new ResponseEntity<>(user, HttpStatus.OK);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}