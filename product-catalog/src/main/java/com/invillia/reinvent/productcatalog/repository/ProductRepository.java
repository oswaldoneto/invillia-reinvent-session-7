package com.invillia.reinvent.productcatalog.repository;

import com.invillia.reinvent.productcatalog.entity.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductRepository extends ElasticsearchRepository<Product, String> {
}
