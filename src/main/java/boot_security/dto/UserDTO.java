package boot_security.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.List;

public class UserDTO {
    
    private Long id;
    
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;
    
    @NotBlank(message = "Имя не может быть пустым")
    private String firstName;
    
    @NotBlank(message = "Фамилия не может быть пустой")
    private String lastName;
    
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;
    
    @Size(min = 4, message = "Пароль должен содержать минимум 4 символа")
    private String password;
    
    @NotEmpty(message = "Необходимо выбрать хотя бы одну роль")
    private Set<Long> roleIds;
    
    private Set<String> roleNames;
    
    private List<RoleDTO> roles;
    
    public UserDTO() {
    }
    
    public UserDTO(Long id, String username, String firstName, String lastName, String email, 
                   Set<Long> roleIds, Set<String> roleNames) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roleIds = roleIds;
        this.roleNames = roleNames;
    }

    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Set<Long> getRoleIds() {
        return roleIds;
    }
    
    public void setRoleIds(Set<Long> roleIds) {
        this.roleIds = roleIds;
    }
    
    public Set<String> getRoleNames() {
        return roleNames;
    }
    
    public void setRoleNames(Set<String> roleNames) {
        this.roleNames = roleNames;
    }
    
    public List<RoleDTO> getRoles() {
        return roles;
    }
    
    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }
}
