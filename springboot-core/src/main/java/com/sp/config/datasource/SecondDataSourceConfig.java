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
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;

/**
 * Created by admin on 2020/4/3.
 */
//@Configuration
//@MapperScan(basePackages = "com.sp.dao.second", sqlSessionFactoryRef = "secondSqlSessionFactory")
public class SecondDataSourceConfig {

    @Bean(name = "secondDS")
    @ConfigurationProperties(prefix = "spring.datasource.ds02")
    public DataSource getDataSource2() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }


    /**
     * 事务管理器2
     * 使用value具体指定使用哪个事务管理器 @Transactional(value="txManager1")
     */
    @Bean(name = "secondTransactionManager")
    public PlatformTransactionManager transactionManager2() {
        return new DataSourceTransactionManager(getDataSource2());
    }

    @Bean(name = "secondSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setTypeAliasesPackage("com.sp.bean.model");
        factory.setDataSource(getDataSource2());
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factory.setMapperLocations(resolver.getResources("classpath:config/mappers/second/*.xml"));
        //factory.setConfigLocation(resolver.getResource("classpath:mybatis/mybatis-config.xml"));

        return factory.getObject();
    }
}
