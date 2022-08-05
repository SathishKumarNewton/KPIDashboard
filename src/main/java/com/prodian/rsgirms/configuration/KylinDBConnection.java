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

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "KylinDSEmFactory",
        transactionManagerRef = "KylinDSTransactionManager",
        basePackages = {
            "com.prodian.rsgirms.dashboard.repository",
            "com.prodian.rsgirms.reports.gwp.repository",
            "com.prodian.rsgirms.scheduler.repository",
            "com.prodian.rsgirms.userapp.repository",
            "com.prodian.rsgirms.usermatrix.repository"
        }
)
public class KylinDBConnection {
    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties KylinDSProperties(){
        return new DataSourceProperties();
    }
    @Primary
    @Bean
    public DataSource KylinDS(@Qualifier("KylinDSProperties") DataSourceProperties kylinDSProperties){
        return kylinDSProperties.initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean KylinDSEmFactory(@Qualifier("KylinDS") DataSource kylinDS, EntityManagerFactoryBuilder builder){
        return builder.dataSource (kylinDS).packages("com.prodian.rsgirms").build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager KylinDSTransactionManager(@Qualifier("KylinDSEmFactory") EntityManagerFactory kylinDSEmFactory){
        return new JpaTransactionManager (kylinDSEmFactory);
    }
}