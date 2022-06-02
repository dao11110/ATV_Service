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
        entityManagerFactoryRef = "b04tensioningEntityManagerFactory",
        transactionManagerRef = "b04tensioningTransactionManager",
        basePackages = "com.foxconn.fii.data.b04tensioning"
)
@EnableTransactionManagement
public class B04TensioningDataSourceConfig {

    @Autowired
    private Environment env;

    @Bean(name = "b04tensioningDSProperties")
    @ConfigurationProperties("b04tensioning.datasource")
    public DataSourceProperties b04tensioningDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "b04tensioningDS")
    @ConfigurationProperties("b04tensioning.datasource.configuration")
    public DataSource b04tensioningDataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "b04tensioningEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean b04tensioningEntityManagerFactory() {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(b04tensioningDataSource(b04tensioningDataSourceProperties()));
        em.setPackagesToScan("com.foxconn.fii.data.b04tensioning");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.properties.hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect", env.getProperty("b04tensioning.datasource.hibernate.dialect"));
        properties.put("hibernate.jdbc.fetch_size", 1000);
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(name = "tensioningTransactionManager")
    public PlatformTransactionManager b04tensioningTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(b04tensioningEntityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean(name = "tensioningJdbcTemplate")
    public JdbcTemplate b04tensioningJdbcTemplate(@Qualifier("b04tensioningDS") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "b04tensioningNamedJdbcTemplate")
    public NamedParameterJdbcTemplate tensioningNamedJdbcTemplate(@Qualifier("b04tensioningDS") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
