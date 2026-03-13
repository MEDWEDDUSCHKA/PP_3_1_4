package boot_security.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import boot_security.models.User;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @GetMapping
    public ModelAndView userPage(Authentication authentication) {
        ModelAndView mav = new ModelAndView("user");
        User user = (User) authentication.getPrincipal();
        mav.addObject("user", user);
        return mav;
    }
    
    @GetMapping("/api/current")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }
}
