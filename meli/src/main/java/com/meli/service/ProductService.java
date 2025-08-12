package com.meli.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meli.model.Product;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProductService {
	private final ObjectMapper mapper = new ObjectMapper();
	private static final String PATHJSON = "data/productos.json";
	private final File productsFile = new File(PATHJSON);
	
	public List<Product> getProducts(){
		if (!productsFile.exists() || productsFile.length() == 0) {
            return new ArrayList<>();
        }
		try {
			return mapper.readValue(productsFile, new TypeReference<List<Product>>() {});
		} catch (Exception e) {
			throw new RuntimeException("Error al leer el archivo json", e);
		}
	}
	
	public void saveProduct(List<Product> products) {
		try {
			productsFile.getParentFile().mkdirs();
            mapper.writerWithDefaultPrettyPrinter().writeValue(productsFile, products);
		} catch (Exception e) {
			throw new RuntimeException("Error al guardar producto", e);
		}
	}
	
	public void addProduct(Product add) {
		List<Product> products = getProducts();
		products.add(add);
		saveProduct(products);
	}
	
	public void updateProduct(String name, Product update) {
		List<Product> products = getProducts();
		for(int i = 0; i < products.size(); i++ ) {
			if(products.get(i).getName().equalsIgnoreCase(name)) {
				products.set(i, update);
				saveProduct(products);
				return;
			}
		}
		throw new RuntimeException("No se encontro producto: "+name);
	}
	
	public void deleteProduct(String name) {
		List<Product> products = getProducts();
        boolean removed = products.removeIf(p -> p.getName().equalsIgnoreCase(name));
        if (removed) {
            saveProduct(products);
        }
	}
}
