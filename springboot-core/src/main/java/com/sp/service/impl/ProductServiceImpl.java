package com.sp.service.impl;

import com.sp.bean.model.Product;
import com.sp.config.datasource.DataSourceType;
import com.sp.config.datasource.TargetDataSourceType;
import com.sp.dao.second.ProductDao;
import com.sp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by admin on 2020/4/3.
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao productDao;
    @Override
    public Product findByName(String name) {
        return productDao.findByName(name);
    }

    @Override
    public void save(Product product) {
        productDao.save(product);
    }
}
