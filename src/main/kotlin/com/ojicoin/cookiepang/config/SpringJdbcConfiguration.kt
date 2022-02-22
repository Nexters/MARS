package com.ojicoin.cookiepang.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource


@Configuration
class SpringJdbcConfiguration {

    @Bean
    fun postgresqlDataSource(
        dataSourceProperties: DataSourceProperties,
        @Value("#{systemEnvironment['DATA_SOURCE_USERNAME']}") username: String,
        @Value("#{systemEnvironment['DATA_SOURCE_PASSWORD']}") password: String,
    ): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName(dataSourceProperties.driverClassName)
        dataSource.url = dataSourceProperties.url
        dataSource.username = username
        dataSource.password = password

        return dataSource
    }
}
