package org.rootservices.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


/**
 * Created by tommackenzie on 5/21/16.
 */
@Configuration
@MapperScan("org.rootservices.authorization.persistence.mapper")
public class PersistenceConfig {
    protected static Logger LOGGER = LogManager.getLogger(PersistenceConfig.class);

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();

        String userName = System.getenv("AUTH_DB_USER");
        String password = System.getenv("AUTH_DB_PASSWORD");
        String connectionUrl = System.getenv("AUTH_DB_URL");

        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUsername(userName);
        dataSource.setUrl(connectionUrl);
        dataSource.setPassword(password);

        return dataSource;
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactory;
        try {
            sqlSessionFactory = new SqlSessionFactoryBean();

            Resource configResource = new ClassPathResource("mybatis-config.xml");

            sqlSessionFactory.setDataSource(dataSource());
            sqlSessionFactory.setTypeAliasesPackage("org.rootservices.authorization.persistence.entity");
            sqlSessionFactory.setConfigLocation(configResource);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        return (SqlSessionFactory) sqlSessionFactory.getObject();
    }
}
