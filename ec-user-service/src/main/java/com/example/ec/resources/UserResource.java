package com.example.ec.resources;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.ec.dtos.user.UserRequestDto;
import com.example.ec.dtos.user.UserResponseDto;
import com.example.ec.exception.EmailException;
import com.example.ec.exception.PasswordException;
import com.example.ec.models.UserModel;
import com.example.ec.servicers.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserResource {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable UUID id) {
        UserResponseDto dto = userService.buildUserResponseDto(userService.findById(id));
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> findAll(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<UserResponseDto> dtos = userService.findAll(pageable).map(userService::buildUserResponseDto);
        return ResponseEntity.ok().body(dtos);
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid UserRequestDto dto) throws EmailException, PasswordException {
        UserModel entity = userService.buildUserModel(dto, null);
        userService.save(entity);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UserRequestDto dto)
            throws EmailException, PasswordException {
        UserModel entity = userService.buildUserModel(dto, id);
        entity.setId(id);
        userService.update(entity);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }
}
