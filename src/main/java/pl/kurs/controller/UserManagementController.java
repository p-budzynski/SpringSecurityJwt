package pl.kurs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.kurs.annotation.IsAdmin;
import pl.kurs.dto.RoleRequest;
import pl.kurs.service.UserService;

import javax.management.relation.RoleNotFoundException;

@RestController
@RequestMapping("/management/users")
@RequiredArgsConstructor
@IsAdmin
public class UserManagementController {
    private final UserService userService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/roles")
    public void assignRole(@PathVariable("id") Long id, @RequestBody RoleRequest request) throws RoleNotFoundException {
        userService.assignRoleToUser(id, request.roleName());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}/roles")
    public void revokeRole(@PathVariable("id") Long id, @RequestBody RoleRequest request) throws RoleNotFoundException {
        userService.removeRoleFromUser(id, request.roleName());
    }
}
