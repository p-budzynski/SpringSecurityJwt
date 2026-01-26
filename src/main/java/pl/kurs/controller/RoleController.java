package pl.kurs.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.kurs.dto.CreateRoleDto;
import pl.kurs.dto.RoleDto;
import pl.kurs.entity.Role;
import pl.kurs.mapper.RoleMapper;
import pl.kurs.service.RoleService;
import pl.kurs.validation.Create;
import pl.kurs.validation.Update;

import javax.management.relation.RoleNotFoundException;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Validated
public class RoleController {
    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable("id") @Min(value = 1, message = "ID must be at least 1") Long id) throws RoleNotFoundException {
        Role role = roleService.getRoleById(id);
        return ResponseEntity.ok(roleMapper.entityToDto(role));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleDto createRole(@RequestBody @Validated(Create.class) CreateRoleDto createRoleDto) {
        return roleService.createRole(createRoleDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDto> updateRole(@PathVariable("id") @Min(value = 1, message = "ID must be at least 1")
            Long id, @RequestBody @Validated(Update.class) CreateRoleDto dto) throws RoleNotFoundException {
        return ResponseEntity.ok(roleService.updateRole(id, dto));
    }

    @DeleteMapping("/{id}")
    public void deleteRoleById(@PathVariable("id") @Min(value = 1, message = "ID must be at least 1") Long id) {
        roleService.deleteRoleById(id);
    }


}
