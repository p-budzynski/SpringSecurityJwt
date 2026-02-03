package pl.kurs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kurs.annotation.IsAdmin;
import pl.kurs.dto.RoleRequest;
import pl.kurs.service.UserService;

import javax.management.relation.RoleNotFoundException;

@RestController
@RequestMapping("/management")
@RequiredArgsConstructor
@IsAdmin
public class UserManagementController {
    private final UserService userService;

    @PostMapping("/{id}/roles")
    public ResponseEntity<Void> assignRole(
            @PathVariable("id") Long id,
            @RequestBody RoleRequest request
            ) throws RoleNotFoundException {
        userService.assignRoleToUser(id, request.roleName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/roles")
    public ResponseEntity<Void> revokeRole(
            @PathVariable("id") Long id,
            @RequestBody RoleRequest request
    ) throws RoleNotFoundException {
        userService.removeRoleFromUser(id, request.roleName());
        return ResponseEntity.noContent().build();
    }
}
