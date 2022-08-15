package com.baglietto.service;

import com.baglietto.dao.entity.Token;
import com.baglietto.dao.entity.User;
import com.baglietto.dao.repository.TokenRepository;
import com.baglietto.dao.repository.UserRepository;
import com.baglietto.exception.UserAlreadyExistsException;
import com.baglietto.restservice.dto.TokenDTO;
import com.baglietto.restservice.dto.UserDTO;
import com.baglietto.utils.Md5Utils;
import org.springframework.stereotype.Service;
import com.baglietto.exception.AuthenticationException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    private final Md5Utils md5Utils;

    public UserService(TokenRepository tokenRepository, UserRepository userRepository) {
        this.md5Utils = new Md5Utils();
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    /**
     * Login del usuario en base a su nombre de usuario y contraseña.
     *
     * @param user
     * @return TokenDTO
     */
    public TokenDTO login(UserDTO user) {
        List<User> users = userRepository.findUserByUsername(user.getUsername());
        String hashedPassword = md5Utils.digest(user.getPassword());

        if (users.isEmpty() || !users.get(0).getPassword().equals(hashedPassword)) {
            throw new AuthenticationException("Usuario o contraseña incorrectos.");
        }
        UUID uuid = UUID.randomUUID();
        tokenRepository.save(new Token(uuid.toString(), new Date(), users.get(0).getId()));

        return new TokenDTO(uuid.toString());
    }

    /**
     * Obtener todos los usuarios.
     *
     * @return Iterable<UserDTO>
     */
    public Iterable<UserDTO> getAll() {
        Iterable<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = new ArrayList<>();

        for (User user: users) {
            userDTOs.add(new UserDTO(user.getUsername(), user.getRole()));
        }
        return userDTOs;
    }

    /**
     * Agregar un usuario nuevo.
     *
     * @param user
     */
    public void add(UserDTO user) {

        List<User> users = userRepository.findUserByUsername(user.getUsername());
        if (!users.isEmpty()) {
            throw new UserAlreadyExistsException("El usuario ya existe");
        }
        userRepository.save(new User(user.getUsername(), md5Utils.digest(user.getPassword()), user.getRole()));
    }
}
