package com.invillia.reinvent.productcatalog.controller;

import com.invillia.reinvent.productcatalog.entity.Product;
import com.invillia.reinvent.productcatalog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(path = "/product")
public class ProductCatalogController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<Product> listProducts() {
        Iterable<Product> productIterable = productRepository.findAll();
        return StreamSupport.stream(productIterable.spliterator(),false).collect(Collectors.toList());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
         Optional<Product> productOptional = productRepository.findById(id);
         if (productOptional.isPresent()) {
             return ResponseEntity.ok().body(productOptional.get());
         }
         return ResponseEntity.notFound().build();
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }
}
