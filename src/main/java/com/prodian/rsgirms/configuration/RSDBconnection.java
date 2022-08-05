package com.prodian.rsgirms.configuration;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.prodian.rsgirms.dashboard.model.GicNicPsqlFunction;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "trainingDSEmFactory",
        transactionManagerRef = "trainingDSTransactionManager",
        basePackages = "com.prodian.rsgirms.dashboard.repository."
)
public class RSDBconnection {
    @Bean
    @ConfigurationProperties("spring.datasource1")
    public DataSourceProperties trainingDSProperties(){
        return new DataSourceProperties();
    }


    @Bean
    public DataSource trainingDS(@Qualifier("trainingDSProperties") DataSourceProperties trainingDSProperties){
        return trainingDSProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean trainingDSEmFactory(
            @Qualifier("trainingDS") DataSource trainingDS,
            EntityManagerFactoryBuilder builder
            ){
        return builder.dataSource(trainingDS).packages(GicNicPsqlFunction.class).build();
    }

    @Bean
    public PlatformTransactionManager trainingDSTransactionManager(@Qualifier("trainingDSEmFactory")EntityManagerFactory trainingDSEmFactory){
        return new JpaTransactionManager(trainingDSEmFactory);
    }
}