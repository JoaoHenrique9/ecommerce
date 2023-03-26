package com.example.ec.models;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(columnDefinition = "uuid")
	private UUID id;
	
	@Column(nullable = false, length = 100)
	private String name;
	
	@Column(nullable = false,  length = 200)
	private String description;
	
	@Column(nullable = false)
	private Long quantity;
	
	@Column(nullable = false)
	private Double price;
	
	@Column(name = "is_enabled", nullable = false)
	private Boolean isEnabled;
	
	@Column(name = "created_at", nullable = false,  updatable = false)
	private Date createdAt;

	@Column(name = "updated_at", nullable = false)
	private Date updatedAt;
	
	@PreUpdate
	public void updateTimeStamps() {
		updatedAt = new Date();
	}
	
	@PrePersist
	public void prePersist(){
		updatedAt = new Date();
		createdAt = new Date();
	}

}
