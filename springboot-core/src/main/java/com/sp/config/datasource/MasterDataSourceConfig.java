package com.sp.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Created by admin on 2020/4/3.
 */
//@Configuration
//@MapperScan(basePackages = "com.sp.dao.master", sqlSessionFactoryRef = "masterSqlSessionFactory")
public class MasterDataSourceConfig{

    @Bean(name = "masterDS")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.ds01")
    public DataSource getDataSource() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }

    /**
     * 指定默认的事务管理器
     * @return
     */
    @Bean(name = "masterTransactionManager")
    @Primary
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(getDataSource());
    }

    @Bean(name = "masterSqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setTypeAliasesPackage("com.sp.bean.model");
        factory.setDataSource(getDataSource());
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factory.setMapperLocations(resolver.getResources("classpath:config/mappers/master/*.xml"));
        //factory.setConfigLocation(resolver.getResource("classpath:mybatis/mybatis-config.xml"));

        return factory.getObject();
    }
}
