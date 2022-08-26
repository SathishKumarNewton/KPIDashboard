package com.prodian.rsgirms.configuration;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "RSDSEmFactory",
        transactionManagerRef = "RSDSTransactionManager",
        basePackages = "com.prodian.rsgirms.dashboard.rsrepository"
)
public class RSDBConnection {
    @Bean
    @ConfigurationProperties("spring.datasource1")
    public DataSourceProperties RSDSProperties(){
        return new DataSourceProperties();
    }

    @Bean
    public DataSource RSDS(@Qualifier("RSDSProperties") DataSourceProperties rsDSProperties){
        return rsDSProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean RSDSEmFactory(
            @Qualifier("RSDS") DataSource rsDS,
            EntityManagerFactoryBuilder builder
            ){
        return builder.dataSource(rsDS).packages("com.prodian.rsgirms.dashboard.modelfunction").build();
    }

    @Bean
    public PlatformTransactionManager RSDSTransactionManager(@Qualifier("RSDSEmFactory")EntityManagerFactory rsDSEmFactory){
        return new JpaTransactionManager(rsDSEmFactory);
    }
}