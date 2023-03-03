package com.brunomilitzer.springbootquartz.component;

import lombok.extern.slf4j.Slf4j;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Slf4j
@Component
public class JobScheduleCreator {

    public CronTrigger createCronTrigger(
            final String triggerName, final Date startTime,
            final String cronExpression, final int misFireInstruction ) {

        final CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setName( triggerName );
        factoryBean.setStartTime( startTime );
        factoryBean.setCronExpression( cronExpression );
        factoryBean.setMisfireInstruction( misFireInstruction );

        try {
            factoryBean.afterPropertiesSet();
        } catch ( final ParseException e ) {
            log.error( e.getMessage(), e );
        }

        return factoryBean.getObject();
    }

    public SimpleTrigger createSimpleTrigger(
            final String triggerName, final Date startTime,
            final Long repeatTime, final int misFireInstruction ) {

        final SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setName( triggerName );
        factoryBean.setStartTime( startTime );
        factoryBean.setRepeatInterval( repeatTime );
        factoryBean.setRepeatCount( SimpleTrigger.REPEAT_INDEFINITELY );
        factoryBean.setMisfireInstruction( misFireInstruction );
        factoryBean.afterPropertiesSet();

        return factoryBean.getObject();
    }

    public JobDetail createJob(
            final Class<? extends QuartzJobBean> jobClass, final boolean isDurable,
            final ApplicationContext context, final String jobName, final String jobGroup ) {

        final JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass( jobClass );
        factoryBean.setDurability( isDurable );
        factoryBean.setApplicationContext( context );
        factoryBean.setName( jobName );
        factoryBean.setGroup( jobGroup );

        // Set job data map
        final JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put( jobName + jobGroup, jobClass.getName() );
        factoryBean.setJobDataMap( jobDataMap );
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

}
