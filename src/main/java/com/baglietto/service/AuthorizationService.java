package com.baglietto.service;

import com.baglietto.dao.entity.Token;
import com.baglietto.dao.entity.User;
import com.baglietto.dao.repository.TokenRepository;
import com.baglietto.dao.repository.UserRepository;
import com.baglietto.exception.TokenNotFoundException;
import com.baglietto.exception.UserWithoutRoleException;
import com.baglietto.restservice.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorizationService {

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";
    public static final String ALL_ROLES = "all";

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    public AuthorizationService(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    /**
     * Valida que el token provisto exista y que además el usuario al que le pertenece tenga los roles solicitados.
     *
     * @param role
     * @param token
     * @return
     */
    public boolean isOk(String role, String token) {

        List<Token> t = tokenRepository.findTokenByUid(token);

        if (t.isEmpty()) {
            throw new TokenNotFoundException("Token inexistente");
        }

        if (!role.equals(ALL_ROLES)) {
            Optional<User> optional = userRepository.findById(t.get(0).getUserId());

            if (optional.isPresent()) {
                User u = optional.get();

                if (!role.equals(u.getRole())) {
                    throw new UserWithoutRoleException("El usuario no tiene el rol necesario");
                }
            }
        }
        return true;
    }

    /**
     * En base a un token de sesión devuelve el usuario al que le pertenece.
     *
     * @param token
     * @return
     */
    public UserDTO getUserByToken(String token) {
        List<Token> t = tokenRepository.findTokenByUid(token);

        if (t.isEmpty()) {
            throw new TokenNotFoundException("Token inexistente");
        }

        Optional<User> optional = userRepository.findById(t.get(0).getUserId());

        if (optional.isPresent()) {
            User u = optional.get();

            return new UserDTO(u.getUsername(), u.getId());
        }
        return null;
    }

    public boolean isValidRole(String role) {
        return role != null && (role.equals(ROLE_ADMIN) || role.equals(ROLE_USER));
    }
}
