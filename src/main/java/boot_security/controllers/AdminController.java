package boot_security.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import boot_security.models.User;
import boot_security.services.RoleService;
import boot_security.services.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private final UserService userService;
    private final RoleService roleService;
    
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    
    @GetMapping
    public String showUserList(Model model, Principal principal) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("allRoles", roleService.getAllRoles());
        
        if (principal != null) {
            User currentUser = userService.findByUsername(principal.getName());
            model.addAttribute("currentUser", currentUser);
        }
        
        return "admin/users";
    }
    
    @GetMapping("/new")
    public String showNewUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/user-form";
    }
    
    @PostMapping
    public String createUser(@RequestParam("firstName") String firstName,
                            @RequestParam("lastName") String lastName,
                            @RequestParam("age") Integer age,
                            @RequestParam("email") String email,
                            @RequestParam("password") String password,
                            @RequestParam("roleIds") List<Long> roleIds) {
        userService.createUser(firstName, lastName, age, email, password, roleIds);    
        return "redirect:/admin";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/user-form";
    }
    
    @PostMapping("/update")
    public String updateUser(@RequestParam("id") Long id,
                            @RequestParam("username") String username,
                            @RequestParam("firstName") String firstName,
                            @RequestParam("lastName") String lastName,
                            @RequestParam("age") Integer age,
                            @RequestParam("email") String email,
                            @RequestParam(value = "password", required = false) String password,
                            @RequestParam("roleIds") List<Long> roleIds) {
        userService.updateUserData(id, firstName, lastName, age, email, password, roleIds);
        return "redirect:/admin";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}
