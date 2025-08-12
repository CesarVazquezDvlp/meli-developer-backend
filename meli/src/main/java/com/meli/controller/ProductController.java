package com.meli.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meli.service.ProductService;

import jakarta.validation.Valid;

import com.meli.model.Product;
import com.meli.response.*;

@RestController
@RequestMapping("/products")
public class ProductController {
	private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
	private static final String METHOD_NAME_LIST_PRODUCTS = "listProducts";
	
	@Autowired
	private ProductService productService;
	
	@GetMapping
	public ResponseEntity<?> listProducts(){
		String trace = UUID.randomUUID().toString().replace("-", "");
		logger.info("Trace: {}. Inicia: {}", folio(trace), METHOD_NAME_LIST_PRODUCTS);
		try {
			ResponseEntity<?> responseEntity;
			List<Product> products = productService.getProducts();
			if (products == null || products.isEmpty()) {
				responseEntity = builderNotFound("No se encontraron productos", "Información no encontrada", folio(trace));
				logger.warn("No se encontraron productos", folio(trace));
			}else {				
				responseEntity = builderSucces("Operación Exitosa.", Map.of("productos", products), folio(trace));
				logger.info("Operación Exitosa.", folio(trace));
			}
			return responseEntity;
		} catch (Exception e) {
			logger.error("Trace: {}. Error inesperado al consultar informacion: {} - {}", folio(trace), e.getMessage(), e);
			return builderServerError("Error inesperado al consultar el estatus del pago.", folio(trace), e.getMessage());
		}
	}
	
	@PostMapping
	public ResponseEntity<?> addProducts(@Valid @RequestBody Product product) {
		String trace = UUID.randomUUID().toString().replace("-", "");
		try {
			ResponseEntity<?> responseEntity;
			if(product.getName() == null || product.getName().isBlank()) {
				responseEntity = builderBadRequest("Parametro incorrecto", "Falta campo nombre", folio(trace));
				logger.warn("Parametros incorrectos", folio(trace));
				return responseEntity;
			}
			if (validExist(product.getName())) {
				responseEntity = builderBadRequest("Ya existe el producto", "El producto ya existe", folio(trace));
				logger.warn("El producto ya existe", folio(trace));
				return responseEntity;
			}			
			productService.addProduct(product);
			responseEntity = builderSucces("Operación Exitosa.", Map.of("producto", product), folio(trace));
			logger.info("Operación Exitosa.", folio(trace));
			return responseEntity;
		} catch (Exception e) {
			logger.error("Trace: {}. Error inesperado al consultar informacion: {} - {}", folio(trace), e.getMessage(), e);
			return builderServerError("Trace: {}. Error inesperado al consultar informacion: {}", folio(trace), e.getMessage());
		}
	}
	
	@PutMapping("/{name}")
	public ResponseEntity<?> updateProducts(@PathVariable("name") String name, @Valid @RequestBody Product product) {
		String trace = UUID.randomUUID().toString().replace("-", "");
		try {
			ResponseEntity<?> responseEntity;
			if(product.getName() == null || product.getName().isBlank()) {
				responseEntity = builderBadRequest("Parametro incorrecto", "Falta campo nombre", folio(trace));
				logger.warn("Parametros incorrectos", folio(trace));
				return responseEntity;
			}
			if (!validExist(name)) {
				responseEntity = builderBadRequest("Producto no encontrado", "Producto no encontrado", folio(trace));
				logger.warn("No existe el producto", folio(trace));
				return responseEntity;
			}	
			productService.updateProduct(name, product);
			responseEntity = builderSucces("Operación Exitosa.", Map.of("producto", product), folio(trace));
			logger.info("Operación Exitosa.", folio(trace));
			return responseEntity;
		} catch (Exception e) {
			logger.error("Trace: {}. Error inesperado al actualizar informacion: {} - {}", folio(trace), e.getMessage(), e);
			return builderServerError("Trace: {}. Error inesperado al actualizar informacion: {}", folio(trace), e.getMessage());
		}
	}
	
	@DeleteMapping("/{name}")
	public ResponseEntity<?> deleteProduct(@PathVariable("name") String name) {
		String trace = UUID.randomUUID().toString().replace("-", "");
		try {
			ResponseEntity<?> responseEntity;
			if(name == null || name.isBlank()) {
				responseEntity = builderBadRequest("Parametro incorrecto", "Falta campo nombre", folio(trace));
				logger.warn("Parametros incorrectos", folio(trace));
				return responseEntity;
			}
			if (!validExist(name)) {
				responseEntity = builderBadRequest("Producto no encontrado", "Producto no encontrado", folio(trace));
				logger.warn("No existe el producto", folio(trace));
				return responseEntity;
			}	
			productService.deleteProduct(name);
			responseEntity = builderSucces("Operación Exitosa.", Map.of("mensaje", "Eliminado correctamente"), folio(trace));
			logger.info("Operación Exitosa.", folio(trace));
			return responseEntity;
		} catch (Exception e) {
			logger.error("Trace: {}. Error inesperado al actualizar informacion: {} - {}", folio(trace), e.getMessage(), e);
			return builderServerError("Trace: {}. Error inesperado al actualizar informacion: {}", folio(trace), e.getMessage());
		}
	}
	
	private ResponseEntity<Succes> builderSucces(String mensaje, Map<String, ?> data, String trace) {
        Succes respuesta = new Succes(mensaje, data);
        respuesta.setFolio(trace);
        logger.info("Trace: {}. Respuesta Succes (200) enviada.", trace);
        logger.debug("Trace: {}. Respuesta Succes (200) enviada.", trace);
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    private ResponseEntity<BadRequest> builderBadRequest(String mensaje, String detalle, String trace) {
        BadRequest respuestaError = new BadRequest(mensaje, trace, detalle);
        respuestaError.setFolio(trace);
        logger.warn("Trace: {}. Respuesta BadRequest (400) enviada. Detalle: {}", trace, detalle);
        logger.debug("Trace: {}. Respuesta BadRequest (400) enviada: {}", trace, respuestaError);
        return new ResponseEntity<>(respuestaError, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<BadRequest> builderNotFound(String mensaje, String detalle, String trace) {
        BadRequest respuestaError = new BadRequest(mensaje, trace, detalle);
        respuestaError.setFolio(trace);
        logger.warn("Trace: {}. Respuesta NotFound (404) enviada. Detalle: {}", trace, detalle);
        logger.debug("Trace: {}. Respuesta NotFound (404) enviada. Detalle: {}", trace, detalle);
        return new ResponseEntity<>(respuestaError, HttpStatus.NOT_FOUND);
    }
	
    private ResponseEntity<ServerError> builderServerError(String errorMessage, String trace, String... details) {
        logger.error("Trace: {}. Error interno del servidor: {}{}", trace, errorMessage, (details.length > 0 ? " Detalles: " + String.join(", ", details) : ""));
        ServerError respuestaError = new ServerError("Problemas al procesar su solicitud, favor de contactar a su administrador.", details.length > 0 ? List.of(details) : List.of(errorMessage));
        respuestaError.setFolio(trace);
        return new ResponseEntity<>(respuestaError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    private boolean validExist(String name) {
    	List<Product> avilables = productService.getProducts();
    	System.out.println("Producto: "+name);
    	System.out.println("Productos: "+avilables);
        if (avilables.stream().anyMatch(x -> x.getName().equalsIgnoreCase(name))) {
            return true;
        } else {
            return false;
        }
    }
    
	private String folio(String trace) {
        return trace.substring(0, 12); 
    }
}
