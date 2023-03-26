package com.example.ec.dtos.product;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.example.ec.models.ProductModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

	public ProductResponseDto(ProductModel entity) {
		this.id = entity.getId();
		this.name = entity.getName();
		this.description = entity.getDescription();
		this.quantity = entity.getQuantity();
		this.price = entity.getPrice();
		this.isEnabled = entity.getIsEnabled();
		this.createdAt = entity.getCreatedAt();
		this.updatedAt = entity.getUpdatedAt();
	}
}
