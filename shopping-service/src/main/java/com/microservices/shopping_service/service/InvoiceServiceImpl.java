package com.microservices.shopping_service.service;

import com.microservices.shopping_service.client.CustomerClient;
import com.microservices.shopping_service.client.ProductClient;
import com.microservices.shopping_service.entity.Invoice;
import com.microservices.shopping_service.entity.InvoiceItem;
import com.microservices.shopping_service.model.Customer;
import com.microservices.shopping_service.model.Product;
import com.microservices.shopping_service.repository.InvoiceItemRepository;
import com.microservices.shopping_service.repository.InvoiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InvoiceServiceImpl implements InvoiceService{

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    InvoiceItemRepository invoiceItemRepository;

    @Autowired
    CustomerClient customerClient;

    @Autowired
    ProductClient productClient;

    @Override
    public List<Invoice> findInvoiceAll() {
        return invoiceRepository.findAll();
    }

    @Override
    public Invoice createInvoice(Invoice invoice) {
        Invoice invoiceDB = invoiceRepository.findByNumberInvoice(invoice.getNumberInvoice());
        if(invoiceDB != null){
            return invoiceDB;
        }
        invoice.setState("CREATED");
        invoiceDB = invoiceRepository.save(invoice);
        invoiceDB.getItems().forEach(invoiceItem -> {
            productClient.updateStockProduct(invoiceItem.getProductId(), invoiceItem.getQuantity() *-1);
        });

        return invoiceDB;
    }

    @Override
    public Invoice updateInvoice(Invoice invoice) {
        Invoice invoiceDB = getInvoice(invoice.getId());
        if(invoiceDB == null){
            return null;
        }
        invoiceDB.setCustomerId(invoice.getCustomerId());
        invoiceDB.setDescription(invoice.getDescription());
        invoiceDB.setNumberInvoice(invoice.getNumberInvoice());
        invoiceDB.getItems().clear();
        invoiceDB.setItems(invoice.getItems());

        return invoiceRepository.save(invoiceDB);
    }

    @Override
    public Invoice deleteInvoice(Invoice invoice) {
        Invoice invoiceDB = getInvoice(invoice.getId());
        if(invoiceDB == null){
            return null;
        }
        invoiceDB.setState("DELETED");
        return invoiceRepository.save(invoiceDB);
    }

    @Override
    public Invoice getInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElse(null);

        if (null != invoice){
            Customer customer = customerClient.getCustomer(invoice.getCustomerId()).getBody();
            invoice.setCustomer(customer);

            List<InvoiceItem> itemList = invoice.getItems().stream().map(invoiceItem -> {
                Product product = productClient.getProduct(invoiceItem.getProductId()).getBody();
                invoiceItem.setProduct(product);
                return invoiceItem;
            }).collect(Collectors.toList());
            invoice.setItems(itemList);
        }
        return invoice;
    }
}
