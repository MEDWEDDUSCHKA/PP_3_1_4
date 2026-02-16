package boot_security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import boot_security.models.Role;
import boot_security.repositories.RoleRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {
    
    private final RoleRepository roleRepository;
    
    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Role getRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Set<Role> getRolesByIds(List<Long> ids) {
        Set<Role> roles = new HashSet<>();
        for (Long id : ids) {
            Role role = roleRepository.findById(id).orElse(null);
            if (role != null) {
                roles.add(role);
            }
        }
        return roles;
    }
}
