package boot_security.controllers;

import boot_security.dto.UserDTO;
import boot_security.mapper.UserMapper;
import boot_security.models.User;
import boot_security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
public class UserRestController {
    
    private final UserService userService;
    private final UserMapper userMapper;
    
    @Autowired
    public UserRestController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        UserDTO userDTO = userMapper.toDTO(user);
        return ResponseEntity.ok(userDTO);
    }
}
