package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.dto.CreateRoleDto;
import pl.kurs.dto.RoleDto;
import pl.kurs.entity.Role;
import pl.kurs.exception.RoleIsExistsException;
import pl.kurs.mapper.RoleMapper;
import pl.kurs.repository.RoleRepository;

import javax.management.relation.RoleNotFoundException;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Transactional
    public RoleDto createRole(CreateRoleDto dto) {
        String normalizedName = dto.getRoleName().toUpperCase();

        if (roleRepository.existsByRoleName(normalizedName)) {
            throw new RoleIsExistsException("Role [" + normalizedName + "] is exists");
        }

        Role role = roleMapper.dtoToEntity(dto);
        role.setRoleName(normalizedName);

        return roleMapper.entityToDto(roleRepository.save(role));
    }

    public Role getRoleById(Long id) throws RoleNotFoundException {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role with id: " + id + " not found"));
    }

    public void deleteRoleById(Long id) {
        roleRepository.deleteById(id);
    }

    @Transactional
    public RoleDto updateRole(Long id, CreateRoleDto dto) throws RoleNotFoundException {
        Role roleToUpdate = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role with id: " + id + " not found"));

        BeanUtils.copyProperties(roleMapper.dtoToEntity(dto), roleToUpdate);

        return roleMapper.entityToDto(roleRepository.save(roleToUpdate));
    }

    public Role findByRoleName(String roleName) throws RoleNotFoundException {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role with name: " + roleName + " not found"));
    }
}
