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
        entityManagerFactoryRef = "f12sfcEntityManagerFactory",
        transactionManagerRef = "f12sfcTransactionManager",
        basePackages = "com.foxconn.fii.data.f12sfc"
)
@EnableTransactionManagement
public class F12SfcDataSourceConfig {

    @Autowired
    private Environment env;

    @Bean(name = "f12sfcDSProperties")
    @ConfigurationProperties("f12sfc.datasource")
    public DataSourceProperties b04sfcDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "f12sfcDS")
    @ConfigurationProperties("f12sfc.datasource.configuration")
    public DataSource b04sfcDataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "f12sfcEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean b04sfcEntityManagerFactory() {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(b04sfcDataSource(b04sfcDataSourceProperties()));
        em.setPackagesToScan("com.foxconn.fii.data.f12sfc");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.properties.hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect", env.getProperty("b04sfc.datasource.hibernate.dialect"));
        properties.put("hibernate.jdbc.fetch_size", 1000);
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(name = "f12sfcTransactionManager")
    public PlatformTransactionManager f12sfcTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(b04sfcEntityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean(name = "f12sfcJdbcTemplate")
    public JdbcTemplate f12sfcJdbcTemplate(@Qualifier("f12sfcDS") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "f12sfcNamedJdbcTemplate")
    public NamedParameterJdbcTemplate f12sfcNamedJdbcTemplate(@Qualifier("f12sfcDS") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
