package com.microservices.shopping_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.shopping_service.entity.Invoice;
import com.microservices.shopping_service.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/invoices")
public class InvoiceRest {

    @Autowired
    InvoiceService invoiceService;

    //Obtener todas las facturas
    @GetMapping
    public ResponseEntity<List<Invoice>> listAllInvoices(){
        List<Invoice> invoices = invoiceService.findInvoiceAll();
        if(invoices.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(invoices);
    }

    //Obtener una factura
    @GetMapping(value = "/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable("id") long id){
        log.info("Fetching invoice with id {}", id);
        Invoice invoice = invoiceService.getInvoice(id);
        if(null == invoice){
            log.error("Invoice with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(invoice);
    }

    //Create a invoice
    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@Valid @RequestBody Invoice invoice, BindingResult result){
        log.info("Create invoice: {}", invoice);
        if(result.hasErrors()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, this.formatMessage(result));
        }
        Invoice invoiceDB = invoiceService.createInvoice(invoice);

        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceDB);
    }

    //Actualizar factura
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateInvoice(@PathVariable("id") long id, @RequestBody Invoice invoice){
        log.info("Updating invoice with id {}", id);
        invoice.setId(id);
        Invoice currentInvoice = invoiceService.updateInvoice(invoice);

        if(currentInvoice==null){
            log.error("Unable to update. Invoice with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(currentInvoice);
    }

    //Eliminar factura
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Invoice> deleteInvoice(@PathVariable("id") long id){
        log.info("Fetching and deleting invoice with id {}", id);

        Invoice invoice = invoiceService.getInvoice(id);
        if(invoice==null){
            log.error("Unable to delete. Invoice with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        invoice = invoiceService.deleteInvoice(invoice);
        return ResponseEntity.ok(invoice);
    }

    private String formatMessage( BindingResult result){
        List<Map<String,String>> errors = result.getFieldErrors().stream()
                .map(err ->{
                    Map<String,String> error =  new HashMap<>();
                    error.put(err.getField(), err.getDefaultMessage());
                    return error;

                }).collect(Collectors.toList());
        ErrorMessage errorMessage = ErrorMessage.builder()
                .code("01")
                .messages(errors).build();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString="";
        try {
            jsonString = mapper.writeValueAsString(errorMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }
}
