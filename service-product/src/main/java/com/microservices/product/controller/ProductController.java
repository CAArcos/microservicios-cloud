package com.microservices.product.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.product.entity.Category;
import com.microservices.product.entity.Product;
import com.microservices.product.service.IProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping (value = "/products")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    //Listar productos por categoria
    @GetMapping
    public ResponseEntity<List<Product>> listProduct(@RequestParam(name = "categoryId", required = false) Long categoryId){

        List<Product> products = new ArrayList<>();
        if(null == categoryId){
          products = iProductService.listAllProduct();
            if (products.isEmpty()){
                return  ResponseEntity.noContent().build();
            }
        }else{
            products = iProductService.findByCategory(Category.builder().id(categoryId).build());
            if (products.isEmpty()){
                return  ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.ok(products);
    }

    //Listar un producto por id
    @GetMapping(value = "/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable("id") Long id){
        Product product = iProductService.getProduct(id);
        if (null == product){
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    //Crear un producto
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product, BindingResult result){
        if(result.hasErrors()){
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, this.formatMessage(result));
        }
        Product productCrete = iProductService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productCrete);
    }

    //Actualizar un producto
    @PutMapping (value = "/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") Long id, @RequestBody Product product){
        product.setId(id);
        Product productDB = iProductService.updateProduct(product);
        if (null == productDB){
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productDB);
    }

    //Eliminar un producto
    @DeleteMapping (value = "/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") Long id){
        Product productDelete = iProductService.deleteProduct(id);
        if (null == productDelete){
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productDelete);
    }

    //Actualizar stock
    @GetMapping(value = "/{id}/stock")
    public ResponseEntity<Product> updateStockProduct(@PathVariable Long id, @RequestParam(name = "quantity", required = true) Double quantity){
        Product product = iProductService.updateStock(id, quantity);
        if (null == product){
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    //Formatear mensages de error
    private String formatMessage(BindingResult result){
        List<Map<String,String>> errors = result.getFieldErrors().stream()
                .map(err -> {
                    Map<String, String> error = new HashMap<>();
                    error.put(err.getField(), err.getDefaultMessage());
                    return error;
                }).collect(Collectors.toList());
        ErrorMessage errorMessage = ErrorMessage.builder()
                .code("01")
                .messages(errors).build();

        ObjectMapper mapper = new ObjectMapper();
        String jsonString ="";
        try {
            jsonString = mapper.writeValueAsString(errorMessage);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return jsonString;
    }
}
