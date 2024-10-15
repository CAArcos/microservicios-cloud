package com.microservices.product;

import com.microservices.product.entity.Category;
import com.microservices.product.entity.Product;
import com.microservices.product.repository.ProductRepository;
import com.microservices.product.service.IProductService;
import com.microservices.product.service.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class ProductserviceMockTest {

    @Mock
    private ProductRepository productRepository;

    private IProductService iProductService;

    //Mock de producto
    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        iProductService = new ProductServiceImpl(productRepository);

        Product computer = Product.builder()
                .id(1L)
                .name("computer")
                .category(Category.builder().id(1L).build())
                .price(Double.parseDouble("12.5"))
                .stock(Double.parseDouble("5"))
                .build();

        Mockito.when(productRepository.findById(1L))
                .thenReturn(Optional.of(computer));
        Mockito.when(productRepository.save(computer)).thenReturn(computer);
    }

    //Busqueda de nuestro producto mockeado
    @Test
    public void whenValidGetID_ThenReturnProduct(){
        Product found = iProductService.getProduct(1L);
        Assertions.assertEquals("computer", found.getName());
    }

    //Verificar stock(valor inicial 5)
    @Test
    public  void whenValidUpdateStock_ThenReturnNewStock(){
        Product newStock = iProductService.updateStock(1L, Double.parseDouble("8"));
        Assertions.assertEquals(13,newStock.getStock());
    }
}
