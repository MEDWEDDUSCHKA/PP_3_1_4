package boot_security.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import boot_security.models.Role;
import boot_security.services.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleRestController {
    
    private final RoleService roleService;
    
    public RoleRestController(RoleService roleService) {
        this.roleService = roleService;
    }
    
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}
