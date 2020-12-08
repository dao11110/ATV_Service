package com.foxconn.fii.config.datasource;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        entityManagerFactoryRef = "b04sfcEntityManagerFactory",
        transactionManagerRef = "b04sfcTransactionManager",
        basePackages = "com.foxconn.fii.data.b04sfc"
)
@EnableTransactionManagement
public class B04SfcDataSourceConfig {

    @Autowired
    private Environment env;

    @Bean(name = "b04sfcDSProperties")
    @ConfigurationProperties("b04sfc.datasource")
    public DataSourceProperties b04sfcDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "b04sfcDS")
    @ConfigurationProperties("b04sfc.datasource.configuration")
    public DataSource b04sfcDataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "b04sfcEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean b04sfcEntityManagerFactory() {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(b04sfcDataSource(b04sfcDataSourceProperties()));
        em.setPackagesToScan("com.foxconn.fii.data.b04sfc");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.properties.hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect", env.getProperty("b04sfc.datasource.hibernate.dialect"));
        properties.put("hibernate.jdbc.fetch_size", 1000);
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(name = "b04sfcTransactionManager")
    public PlatformTransactionManager b04sfcTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(b04sfcEntityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean(name = "b04sfcJdbcTemplate")
    public JdbcTemplate b04sfcJdbcTemplate(@Qualifier("b04sfcDS") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "b04sfcNamedJdbcTemplate")
    public NamedParameterJdbcTemplate b04sfcNamedJdbcTemplate(@Qualifier("b04sfcDS") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
