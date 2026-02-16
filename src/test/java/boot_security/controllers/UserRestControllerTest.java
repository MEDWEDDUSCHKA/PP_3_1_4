package boot_security.controllers;

import boot_security.dto.UserDTO;
import boot_security.mapper.UserMapper;
import boot_security.models.Role;
import boot_security.models.User;
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
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit тесты для UserRestController.
 * Проверяют корректность работы эндпоинта профиля пользователя.
 */
@WebMvcTest(controllers = UserRestController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
    },
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {boot_security.configs.WebSecurityConfig.class}
    ))
class UserRestControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private UserMapper userMapper;
    
    private User testUser;
    private UserDTO testUserDTO;
    private Role testRole;
    
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
    }
    
    /**
     * Тест успешного получения профиля аутентифицированного пользователя
     */
    @Test
    void testGetProfile_Success() throws Exception {
        // Arrange
        Principal mockPrincipal = () -> "testuser";
        
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);
        
        // Act & Assert
        mockMvc.perform(get("/api/user/profile")
                .principal(mockPrincipal))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.username", is("testuser")))
            .andExpect(jsonPath("$.firstName", is("Test")))
            .andExpect(jsonPath("$.lastName", is("User")))
            .andExpect(jsonPath("$.email", is("test@example.com")));
        
        verify(userService, times(1)).findByUsername("testuser");
        verify(userMapper, times(1)).toDTO(testUser);
    }
}
