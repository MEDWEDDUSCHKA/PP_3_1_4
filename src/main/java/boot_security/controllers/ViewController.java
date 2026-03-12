package boot_security.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import boot_security.models.User;
import boot_security.services.RoleService;
import boot_security.services.UserService;

import java.security.Principal;

@Controller
public class ViewController {
    
    private final UserService userService;
    private final RoleService roleService;
    
    public ViewController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    
    @GetMapping("/admin")
    public String adminPage(Model model, Principal principal) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("allRoles", roleService.getAllRoles());
        
        User currentUser = userService.findByUsername(principal.getName());
        model.addAttribute("currentUser", currentUser);
        return "admin/users";
    }
    
    @GetMapping("/user")
    public String userPage(Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);
        return "user";
    }
    
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
