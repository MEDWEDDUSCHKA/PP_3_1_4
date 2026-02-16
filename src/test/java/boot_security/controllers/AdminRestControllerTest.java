package boot_security.controllers;

import boot_security.dto.RoleDTO;
import boot_security.dto.UserDTO;
import boot_security.mapper.UserMapper;
import boot_security.models.Role;
import boot_security.models.User;
import boot_security.services.RoleService;
import boot_security.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit тесты для AdminRestController.
 * Проверяют корректность работы всех REST эндпоинтов для управления пользователями.
 */
@WebMvcTest(controllers = AdminRestController.class, 
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
    },
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {boot_security.configs.WebSecurityConfig.class}
    ))
class AdminRestControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private RoleService roleService;
    
    @MockBean
    private UserMapper userMapper;
    
    private User testUser;
    private UserDTO testUserDTO;
    private Role testRole;
    private RoleDTO testRoleDTO;
    
    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        testRole = new Role("ROLE_USER");
        testRole.setId(1L);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRoles(Set.of(testRole));
        
        testUserDTO = new UserDTO();
        testUserDTO.setId(1L);
        testUserDTO.setUsername("testuser");
        testUserDTO.setFirstName("Test");
        testUserDTO.setLastName("User");
        testUserDTO.setEmail("test@example.com");
        testUserDTO.setRoleIds(Set.of(1L));
        testUserDTO.setRoleNames(Set.of("ROLE_USER"));
        
        testRoleDTO = new RoleDTO(1L, "ROLE_USER");
    }
    
    /**
     * Тест успешного получения всех пользователей
     */
    @Test
    void testGetAllUsers_Success() throws Exception {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        List<UserDTO> userDTOs = Arrays.asList(testUserDTO);
        
        when(userService.getAllUsers()).thenReturn(users);
        when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);
        
        // Act & Assert
        mockMvc.perform(get("/api/admin/users"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].username", is("testuser")))
            .andExpect(jsonPath("$[0].email", is("test@example.com")));
        
        verify(userService, times(1)).getAllUsers();
    }
    
    /**
     * Тест успешного получения пользователя по ID
     */
    @Test
    void testGetUserById_Success() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);
        
        // Act & Assert
        mockMvc.perform(get("/api/admin/users/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.username", is("testuser")))
            .andExpect(jsonPath("$.email", is("test@example.com")));
        
        verify(userService, times(1)).getUserById(1L);
    }
    
    /**
     * Тест ошибки 404 при запросе несуществующего пользователя
     */
    @Test
    void testGetUserById_NotFound() throws Exception {
        // Arrange
        when(userService.getUserById(999L)).thenReturn(null);
        
        // Act & Assert
        mockMvc.perform(get("/api/admin/users/999"))
            .andExpect(status().isNotFound());
        
        verify(userService, times(1)).getUserById(999L);
    }
    
    /**
     * Тест успешного создания нового пользователя
     */
    @Test
    void testCreateUser_Success() throws Exception {
        // Arrange
        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setUsername("newuser");
        newUserDTO.setFirstName("New");
        newUserDTO.setLastName("User");
        newUserDTO.setEmail("new@example.com");
        newUserDTO.setPassword("password123");
        newUserDTO.setRoleIds(Set.of(1L));
        
        when(roleService.getRolesByIds(anyList())).thenReturn(Set.of(testRole));
        when(userMapper.toEntity(any(UserDTO.class), any())).thenReturn(testUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);
        doNothing().when(userService).saveUser(any(User.class));
        
        // Act & Assert
        mockMvc.perform(post("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUserDTO)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.username", is("testuser")));
        
        verify(userService, times(1)).saveUser(any(User.class));
    }
    
    /**
     * Тест успешного обновления существующего пользователя
     */
    @Test
    void testUpdateUser_Success() throws Exception {
        // Arrange
        UserDTO updateUserDTO = new UserDTO();
        updateUserDTO.setUsername("updateduser");
        updateUserDTO.setFirstName("Updated");
        updateUserDTO.setLastName("User");
        updateUserDTO.setEmail("updated@example.com");
        updateUserDTO.setRoleIds(Set.of(1L));
        
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(roleService.getRolesByIds(anyList())).thenReturn(Set.of(testRole));
        doNothing().when(userMapper).updateEntity(any(User.class), any(UserDTO.class), any());
        doNothing().when(userService).updateUser(any(User.class));
        when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);
        
        // Act & Assert
        mockMvc.perform(put("/api/admin/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserDTO)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        
        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).updateUser(any(User.class));
    }
    
    /**
     * Тест ошибки при обновлении несуществующего пользователя
     */
    @Test
    void testUpdateUser_NotFound() throws Exception {
        // Arrange
        UserDTO updateUserDTO = new UserDTO();
        updateUserDTO.setUsername("updateduser");
        updateUserDTO.setFirstName("Updated");
        updateUserDTO.setLastName("User");
        updateUserDTO.setEmail("updated@example.com");
        updateUserDTO.setRoleIds(Set.of(1L));
        
        when(userService.getUserById(999L)).thenReturn(null);
        
        // Act & Assert
        mockMvc.perform(put("/api/admin/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserDTO)))
            .andExpect(status().isNotFound());
        
        verify(userService, times(1)).getUserById(999L);
        verify(userService, never()).updateUser(any(User.class));
    }
    
    /**
     * Тест успешного удаления пользователя
     */
    @Test
    void testDeleteUser_Success() throws Exception {
        // Arrange
        doNothing().when(userService).deleteUser(1L);
        
        // Act & Assert
        mockMvc.perform(delete("/api/admin/users/1"))
            .andExpect(status().isNoContent());
        
        verify(userService, times(1)).deleteUser(1L);
    }
    
    /**
     * Тест успешного получения всех ролей
     */
    @Test
    void testGetAllRoles_Success() throws Exception {
        // Arrange
        List<Role> roles = Arrays.asList(testRole);
        when(roleService.getAllRoles()).thenReturn(roles);
        
        // Act & Assert
        mockMvc.perform(get("/api/admin/roles"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].name", is("ROLE_USER")));
        
        verify(roleService, times(1)).getAllRoles();
    }
}
