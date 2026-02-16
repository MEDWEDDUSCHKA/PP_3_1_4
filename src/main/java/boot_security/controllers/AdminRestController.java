package boot_security.controllers;

import boot_security.dto.RoleDTO;
import boot_security.dto.UserDTO;
import boot_security.mapper.UserMapper;
import boot_security.models.Role;
import boot_security.models.User;
import boot_security.services.RoleService;
import boot_security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    
    private final UserService userService;
    private final RoleService roleService;
    private final UserMapper userMapper;
    
    @Autowired
    public AdminRestController(UserService userService, RoleService roleService, UserMapper userMapper) {
        this.userService = userService;
        this.roleService = roleService;
        this.userMapper = userMapper;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream().map(userMapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь с ID " + id + " не найден");
        }
        UserDTO userDTO = userMapper.toDTO(user);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        Set<Role> roles = roleService.getRolesByIds(userDTO.getRoleIds().stream().collect(Collectors.toList()));

        User user = userMapper.toEntity(userDTO, roles);

        userService.saveUser(user);

        UserDTO createdUserDTO = userMapper.toDTO(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserDTO);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            throw new EntityNotFoundException("Пользователь с ID " + id + " не найден");
        }

        Set<Role> roles = roleService.getRolesByIds(userDTO.getRoleIds().stream().collect(Collectors.toList()));

        userMapper.updateEntity(existingUser, userDTO, roles);

        userService.updateUser(existingUser);

        UserDTO updatedUserDTO = userMapper.toDTO(existingUser);
        return ResponseEntity.ok(updatedUserDTO);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        List<RoleDTO> roleDTOs = roles.stream().map(role -> new RoleDTO(role.getId(), role.getName())).collect(Collectors.toList());
        return ResponseEntity.ok(roleDTOs);
    }
}
