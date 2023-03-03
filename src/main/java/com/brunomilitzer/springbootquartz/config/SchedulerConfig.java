package com.brunomilitzer.springbootquartz.config;

import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class SchedulerConfig {

    private final DataSource dataSource;

    private final ApplicationContext applicationContext;

    private final QuartzProperties quartzProperties;

    public SchedulerConfig(
            final DataSource dataSource,
            final ApplicationContext applicationContext,
            final QuartzProperties quartzProperties ) {
        this.dataSource = dataSource;
        this.applicationContext = applicationContext;
        this.quartzProperties = quartzProperties;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        final Properties properties = new Properties();
        properties.putAll( this.quartzProperties.getProperties() );

        final SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setOverwriteExistingJobs( true );
        factoryBean.setDataSource( this.dataSource );
        factoryBean.setQuartzProperties( properties );

        return factoryBean;
    }

}
