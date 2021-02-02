package com.foxconn.fii.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "b04stencilEntityManagerFactory",
        transactionManagerRef = "b04stencilTransactionManager",
        basePackages = "com.foxconn.fii.data.b04stencil"
)
@EnableTransactionManagement
public class B04StencilDataSourceConfig {
    @Autowired
    private Environment env;

    @Bean(name = "b04stencilDSProperties")
    @ConfigurationProperties("b04stencil.datasource")
    public DataSourceProperties b04stencilDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "b04stencilDS")
    @ConfigurationProperties("b04stencil.datasource.configuration")
    public DataSource b04stencilDataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "b04stencilEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean b04stencilEntityManagerFactory() {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(b04stencilDataSource(b04stencilDataSourceProperties()));
        em.setPackagesToScan("com.foxconn.fii.data.b04stencil");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.properties.hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect", env.getProperty("b04stencil.datasource.hibernate.dialect"));
        //properties.put("hibernate.jdbc.fetch_size", 1000);
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(name = "b04stencilTransactionManager")
    public PlatformTransactionManager b04stencilTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(b04stencilEntityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean(name = "b04stencilJdbcTemplate")
    public JdbcTemplate b04stencilJdbcTemplate(@Qualifier("b04stencilDS") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Primary
    @Bean(name = "b04stencilNamedJdbcTemplate")
    public NamedParameterJdbcTemplate b04stencilNamedJdbcTemplate(@Qualifier("b04stencilDS") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
