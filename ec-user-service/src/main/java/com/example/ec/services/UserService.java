package com.example.ec.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.ec.dtos.user.UserRequestDto;
import com.example.ec.dtos.user.UserResponseDto;
import com.example.ec.exception.EmailException;
import com.example.ec.exception.ObjectNotFoundException;
import com.example.ec.exception.PasswordException;
import com.example.ec.models.UserModel;
import com.example.ec.repositories.UserRepository;
import com.example.ec.utils.ObjectMapperUtils;
import com.example.ec.utils.StringUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserModel findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Usuario não encontrado"));
    }

    public Page<UserModel> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public UserModel save(UserModel entity) {
        return userRepository.save(entity);
    }

    public void delete(UUID id) {
        UserModel entity = findById(id);
        userRepository.delete(entity);
    }

    public UserModel update(UserModel entity) {
        UserModel newEntity = findById(entity.getId());
        updateUserModel(entity, newEntity);
        return userRepository.save(newEntity);
    }

    public UserModel findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void validateUser(String email, UUID id) throws EmailException {
        UserModel user = findByEmail(email);

        if (user != null && (id == null || !id.equals(user.getId())))
            throw new EmailException("Já possui um usuario cadastrado com esse email");
    }

    private String buildPassword(String password) throws PasswordException {
        if (password.length() < 6)
            throw new PasswordException("A senha deve ter pelo menos 6 caracteres");

        return StringUtils.encoder(password);
    }

    public UserModel buildUserModel(UserRequestDto dto, UUID id) throws EmailException, PasswordException {
        validateUser(dto.getEmail(), id);
        if (id == null && (dto.getPassword() == null || dto.getPassword().isEmpty()))
            throw new PasswordException("A senha não pode ser vazia");
        else if (id != null && dto.getPassword().isEmpty())
            dto.setPassword(null);
        else {
            String encodedPassword = buildPassword(dto.getPassword());
            dto.setPassword(encodedPassword);
        }

        return ObjectMapperUtils.map(dto, UserModel.class);
    }

    public UserResponseDto buildUserResponseDto(UserModel entity) {
        return ObjectMapperUtils.map(entity, UserResponseDto.class);
    }

    public void updateUserModel(UserModel entity, UserModel newEntity) {
        ObjectMapperUtils.map(entity, newEntity);
    }

}
