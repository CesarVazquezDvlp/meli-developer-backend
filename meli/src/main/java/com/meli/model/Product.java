package com.meli.model;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Product {
	
	@NotBlank(message = "El nombre no puede estar vacio")
	private String name;
	
	@NotBlank(message = "La descripción no puede estar vacia")
	private String description;
	
	@NotNull(message = "El precio no puede estar vacio")
	private double price;
	
	@NotNull(message = "La calificación no puede estar vacia")
	private double qualification;
	
	@NotNull(message = "Las especificaciones no pueden estar vacias")
	private Map<String, String> specs;
	
	@NotNull(message = "Las imagenes no pueden estar vacias")
	private String[] imagen;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getQualification() {
		return qualification;
	}
	public void setQualification(double qualification) {
		this.qualification = qualification;
	}
	public Map<String, String> getSpecs() {
		return specs;
	}
	public void setSpecs(Map<String, String> specs) {
		this.specs = specs;
	}
	public String[] getImagen() {
		return imagen;
	}
	public void setImagen(String[] imagen) {
		this.imagen = imagen;
	}
}
