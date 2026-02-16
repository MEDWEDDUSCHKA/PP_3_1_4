package boot_security.mapper;

import boot_security.dto.RoleDTO;
import boot_security.dto.UserDTO;
import boot_security.models.Role;
import boot_security.models.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());

        if (user.getRoles() != null) {
            dto.setRoleIds(user.getRoles().stream().map(Role::getId).collect(Collectors.toSet()));
            
            dto.setRoleNames(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

            dto.setRoles(user.getRoles().stream().map(role -> new RoleDTO(role.getId(), role.getName())).collect(Collectors.toList()));
        }

        return dto;
    }

    public User toEntity(UserDTO dto, Set<Role> roles) {
        if (dto == null) {
            return null;
        }
        
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(dto.getPassword());
        }
        
        user.setRoles(roles);
        return user;
    }

    public void updateEntity(User user, UserDTO dto, Set<Role> roles) {
        if (user == null || dto == null) {
            return;
        }
        
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(dto.getPassword());
        }
        
        user.setRoles(roles);
    }
}
