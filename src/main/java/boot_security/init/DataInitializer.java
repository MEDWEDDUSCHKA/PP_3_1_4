package boot_security.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import boot_security.dto.UserDTO;
import boot_security.models.Role;
import boot_security.repositories.RoleRepository;
import boot_security.services.UserService;
import java.util.Arrays;
import java.util.List;


@Component
public class DataInitializer implements CommandLineRunner {
    
    private final UserService userService;
    private final RoleRepository roleRepository;
    
    public DataInitializer(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN");
        if (roleAdmin == null) {
            roleAdmin = new Role("ROLE_ADMIN");
            roleRepository.save(roleAdmin);
            System.out.println("Created ROLE_ADMIN");
        }
        
        Role roleUser = roleRepository.findByName("ROLE_USER");
        if (roleUser == null) {
            roleUser = new Role("ROLE_USER");
            roleRepository.save(roleUser);
            System.out.println("Created ROLE_USER");
        }

        if (userService.findByEmail("admin@mail.ru") == null) {
            UserDTO adminDTO = new UserDTO("admin", "admin", "admin@mail.ru", "admin", 35, Arrays.asList(roleAdmin.getId(), roleUser.getId()));
            userService.createUser(adminDTO);
            System.out.println("Created admin user with email: admin@mail.ru, password: admin");
        } else {
            System.out.println("Admin user already exists");
        }

        if (userService.findByEmail("user@mail.ru") == null) {
            UserDTO userDTO = new UserDTO("user", "user", "user@mail.ru", "user", 30, Arrays.asList(roleUser.getId()));
            userService.createUser(userDTO);
            System.out.println("Created user with email: user@mail.ru, password: user");
        } else {
            System.out.println("User 'user' already exists");
        }
    }
}
