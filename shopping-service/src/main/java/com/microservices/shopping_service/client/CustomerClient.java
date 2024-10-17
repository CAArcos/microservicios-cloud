package com.microservices.shopping_service.client;

import com.microservices.shopping_service.model.Customer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name = "customer-service", path = "/customers")
public interface CustomerClient {
    @CircuitBreaker(name = "customerCB", fallbackMethod = "fallbackGetCustomer")
    @GetMapping(value = "/{id}")
    ResponseEntity<Customer> getCustomer(@PathVariable("id") Long id);

    default ResponseEntity<Customer> fallbackGetCustomer(@PathVariable("id") Long id, Throwable ex) {
        Customer customer = Customer.builder()
                .firstName("none")
                .lastName("none")
                .email("none")
                .photoUrl("none").build();
        return ResponseEntity.ok(customer);
    }

 }
