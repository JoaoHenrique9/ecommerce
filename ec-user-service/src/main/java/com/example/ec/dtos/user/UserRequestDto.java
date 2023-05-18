package com.example.ec.dtos.user;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.example.ec.dtos.role.RoleRequestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserRequestDto {

    @NotNull
    @Length(min = 2, max = 150, message = "O nome tem que ter entre 2 e 150 caracteres")
    private String name;

    @Email
    private String email;

    @NotNull
    private String password;

    private List<RoleRequestDto> roles;
}
