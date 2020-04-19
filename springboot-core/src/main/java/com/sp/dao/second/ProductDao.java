package com.sp.dao.second;

import com.sp.bean.model.Product;
import com.sp.config.datasource.DataSourceType;
import com.sp.config.datasource.TargetDataSourceType;

public interface ProductDao {

    @TargetDataSourceType(dataType = DataSourceType.second)
    Product findByName(String name);

    @TargetDataSourceType(dataType = DataSourceType.second)
    void save(Product product);
}
