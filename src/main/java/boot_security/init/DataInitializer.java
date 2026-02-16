package boot_security.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import boot_security.models.Role;
import boot_security.models.User;
import boot_security.repositories.RoleRepository;
import boot_security.services.UserService;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final UserService userService;
    private final RoleRepository roleRepository;
    
    @Autowired
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
        }
        
        Role roleUser = roleRepository.findByName("ROLE_USER");
        if (roleUser == null) {
            roleUser = new Role("ROLE_USER");
            roleRepository.save(roleUser);
        }

        if (userService.findByUsername("admin") == null) {
            User admin = new User("admin", "admin", "Admin", "Adminov", "admin@example.com");
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(roleAdmin);
            adminRoles.add(roleUser);
            admin.setRoles(adminRoles);
            userService.saveUser(admin);
        }

        if (userService.findByUsername("user") == null) {
            User user = new User("user", "user", "User", "Userov", "user@example.com");
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(roleUser);
            user.setRoles(userRoles);
            userService.saveUser(user);
        }
    }
}
