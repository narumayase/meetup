package com.baglietto.restservice;

import com.baglietto.exception.NotValidRoleException;
import com.baglietto.restservice.dto.TokenDTO;
import com.baglietto.restservice.dto.UserDTO;
import com.baglietto.service.AuthorizationService;
import com.baglietto.service.UserService;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path = "/user")
@RestController
public class UserController {

    private final AuthorizationService authorizationService;
    private final UserService userService;

    public UserController(AuthorizationService authorizationService, UserService userService) {
        this.authorizationService = authorizationService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public TokenDTO login(@RequestBody UserDTO user) {
        try {
            return userService.login(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping()
    public @ResponseBody
    Iterable<UserDTO> getAll(@RequestHeader("authorization") String token) {
        try {
            authorizationService.isOk(AuthorizationService.ROLE_ADMIN, token);
            return userService.getAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping()
    public @ResponseBody
    void add(@RequestHeader("authorization") String token, @RequestBody UserDTO user) {
        try {
            authorizationService.isOk(AuthorizationService.ROLE_ADMIN, token);

            if (!authorizationService.isValidRole(user.getRole())) {
                throw new NotValidRoleException("El rol solicitado no existe.");
            }

            userService.add(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
