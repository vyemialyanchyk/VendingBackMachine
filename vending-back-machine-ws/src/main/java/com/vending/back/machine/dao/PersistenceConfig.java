package com.vending.back.machine.dao;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 */
@Configuration
@EnableTransactionManagement
@MapperScan("com.vending.back.machine.mapper")
@ComponentScan("com.vending.back.machine")
@EnableAspectJAutoProxy
public class PersistenceConfig {

	@Value("classpath:mybatis-config.xml")
	protected Resource myBatisConfig;

	@Autowired
	protected DataSource dataSource;

	@Bean
	public DataSourceTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	public SqlSessionFactoryBean sqlSessionFactory() {
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setTypeAliasesPackage("com.vending.back.machine.mapper");
		sessionFactory.setTypeHandlersPackage("com.github.javaplugs.mybatis");
		sessionFactory.setConfigLocation(myBatisConfig);
		return sessionFactory;
	}

}
