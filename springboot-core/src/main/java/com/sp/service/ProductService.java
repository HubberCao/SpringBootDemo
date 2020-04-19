package com.sp.service;

import com.sp.bean.model.Product;

public interface ProductService {

    Product findByName(String name);

    void save(Product product);
}
