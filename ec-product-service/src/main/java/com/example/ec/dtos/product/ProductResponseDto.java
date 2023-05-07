package com.example.ec.dtos.product;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.example.ec.dtos.category.CategoryResponseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
@JsonInclude(Include.NON_NULL)
public class ProductResponseDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private UUID id;

	private String name;

	private String description;

	private Long quantity;

	private Double price;

	private Boolean isEnabled;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Fortaleza")
	private Date createdAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Fortaleza")
	private Date updatedAt;

	private List<CategoryResponseDto> categories;

}
