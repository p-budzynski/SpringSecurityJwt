package pl.kurs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kurs.service.UserService;

import javax.management.relation.RoleNotFoundException;

@RestController
@RequestMapping("/management")
@RequiredArgsConstructor
public class UserManagementController {
    private final UserService userService;

    @PostMapping("/{username}/roles")
    public ResponseEntity<Void> assignRole(@PathVariable("username") String username, @RequestBody String roleName) throws RoleNotFoundException {
        userService.assignRoleToUser(username, roleName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{username}/roles")
    public ResponseEntity<Void> revokeRoles(@PathVariable("username") String username, @RequestBody String roleName) throws RoleNotFoundException {
        userService.removeRoleFromUser(username, roleName);
        return ResponseEntity.noContent().build();
    }
}
