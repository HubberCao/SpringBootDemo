package com.sp.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;


/**
 * Created by admin on 2019/12/4.
 */


//@EnableTransactionManagement // 开启注解事务管理，等同于xml配置文件中的 <tx:annotation-driven />

// 导入xml方式的事务配置， 需要注销该类方法，如getDataSource等
//@ImportResource("classpath:transaction.xml")

// @MapperScan("com.sp.dao")
// @Configuration
public class DataSourceConfig implements TransactionManagementConfigurer {

    /////////////////////////数据源配置方式1////////////////////////////////////////////////
    @Autowired
    private Environment env;
    @Bean
    public DataSource getDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();

        druidDataSource.setUrl(env.getProperty("my.datasource.url"));
        druidDataSource.setUsername(env.getProperty("my.datasource.username"));
        druidDataSource.setPassword(env.getProperty("my.datasource.password"));
        return druidDataSource;
    }

    /////////////////////////数据源配置方式2////////////////////////////////////////////////
    @Bean
    @ConfigurationProperties(prefix = "my.datasource")
    public DataSource getDataSource2() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }

    /**
     * 指定默认的事务管理器
     * @return
     */
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(getDataSource());
    }

    /**
     * 事务管理器2
     * 使用value具体指定使用哪个事务管理器 @Transactional(value="txManager1")
     */
    @Bean(name = "txManager2")
    public PlatformTransactionManager transactionManager2() {
        return new DataSourceTransactionManager(getDataSource2());
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(getDataSource());
        factory.setTypeAliasesPackage("com.sp.bean.model");
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factory.setMapperLocations(resolver.getResources("classpath:config/mappers*//**//*.xml"));
        //factory.setConfigLocation(resolver.getResource("classpath:mybatis/mybatis-config.xml"));

        return factory.getObject();
    }

}
