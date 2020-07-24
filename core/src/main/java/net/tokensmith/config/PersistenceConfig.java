package net.tokensmith.config;


import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


/**
 * Created by tommackenzie on 5/21/16.
 */
@Configuration
@MapperScan("net.tokensmith.authorization.persistence.mapper")
@PropertySource({"classpath:application-${spring.profiles.active:default}.properties"})
public class PersistenceConfig {
    protected static Logger LOGGER = LoggerFactory.getLogger(PersistenceConfig.class);

    @Value("${auth.db.user}")
    private String userName;
    @Value("${auth.db.password}")
    private String password;
    @Value("${auth.db.url}")
    private String connectionUrl;

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();

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
            sqlSessionFactory.setTypeAliasesPackage("net.tokensmith.authorization.persistence.entity");
            sqlSessionFactory.setConfigLocation(configResource);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        return (SqlSessionFactory) sqlSessionFactory.getObject();
    }
}
