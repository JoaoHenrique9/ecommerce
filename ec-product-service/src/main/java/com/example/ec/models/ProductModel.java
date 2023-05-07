package com.example.ec.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Audited
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(columnDefinition = "uuid")
	private UUID id;

	@Column(nullable = false, length = 150)
	private String name;

	@Column(nullable = false, length = 500)
	private String description;

	@Column(nullable = false)
	private Integer quantity;

	@Column(nullable = false)
	private Double price;

	@Column(name = "is_enabled", nullable = false)
	private Boolean isEnabled;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Date createdAt;

	@Column(name = "updated_at", nullable = false)
	private Date updatedAt;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "product_categories", joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"))
	private List<CategoryModel> categories;

	public ProductModel(UUID id, String name) {
		this.id = id;
		this.name = name;
	}

	public ProductModel(UUID id, String name, String description, Integer quantity, Double price) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.quantity = quantity;
		this.price = price;
	}

	@PreUpdate
	public void updateTimeStamps() {
		updatedAt = new Date();
	}

	@PrePersist
	public void prePersist() {
		updatedAt = new Date();
		createdAt = new Date();
	}
}
