package com.example.ec.dtos.product;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductRequestDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull
	private String name;

	@NotNull
	@Length(min = 5, max = 120, message = "O tamanho deve ser entre 5 e 200 caracteres")
	private String description;

	@NotNull
	@Min(value = 1, message = "Tem que possui pelo menos 1 produto")
	private Long quantity;

	@NotNull
	@Min(value = 1, message = "preço tem que ser maior que 0 ")
	private Double price;

	@NotNull
	private Boolean isEnabled;

}
