package com.microservices.shopping_service.entity;

import com.microservices.shopping_service.model.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_invoice_items")
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Positive(message = "El stock debe ser mayor a cero")
    private Double quantity;
    private Double price;

    @Column(name = "product_id")
    private Long productId;

    @Transient
    private Double subTotal;

    @Transient
    private Product product;

    public Double getSubTotal(){
        if(this.price >0 && this.quantity > 0){
            return this.quantity * this.price;
        }else{
            return (double) 0;
        }
    }

    public InvoiceItem(){
        this.quantity=(double) 0;
        this.price=(double) 0;
    }

}
