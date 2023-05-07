package com.example.ec.dtos.category;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CategoryRequestDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Length(min = 2, max = 150, message = "O nome tem que ter entre 2 e 150 caracteres")
	private String name;
}