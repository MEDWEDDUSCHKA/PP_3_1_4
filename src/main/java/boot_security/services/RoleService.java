package boot_security.services;

import boot_security.models.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {
    List<Role> getAllRoles();
    Role getRoleById(Long id);
    Set<Role> getRolesByIds(List<Long> ids);
}
