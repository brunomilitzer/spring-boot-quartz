package com.brunomilitzer.springbootquartz.service;

import com.brunomilitzer.springbootquartz.component.JobScheduleCreator;
import com.brunomilitzer.springbootquartz.enumeration.JobStatus;
import com.brunomilitzer.springbootquartz.job.SimpleCronJob;
import com.brunomilitzer.springbootquartz.job.SimpleJob;
import com.brunomilitzer.springbootquartz.model.SchedulerJobInfo;
import com.brunomilitzer.springbootquartz.repository.SchedulerRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Transactional
@Service
public class SchedulerJobService {

    private final Scheduler scheduler;

    private final SchedulerFactoryBean schedulerFactoryBean;

    private final SchedulerRepository schedulerRepository;

    private final ApplicationContext context;

    private final JobScheduleCreator scheduleCreator;

    @Autowired
    public SchedulerJobService( final Scheduler scheduler, final SchedulerFactoryBean schedulerFactoryBean,
                                final SchedulerRepository schedulerRepository, final ApplicationContext context,
                                final JobScheduleCreator scheduleCreator ) {

        this.scheduler = scheduler;
        this.schedulerFactoryBean = schedulerFactoryBean;
        this.schedulerRepository = schedulerRepository;
        this.context = context;
        this.scheduleCreator = scheduleCreator;
    }

    public boolean startJobNow( final SchedulerJobInfo jobInfo ) {

        try {
            final SchedulerJobInfo getJobInfo = this.schedulerRepository.findByJobName( jobInfo.getJobName() );
            getJobInfo.setJobStatus( JobStatus.STARTED );
            this.schedulerRepository.save( getJobInfo );
            this.schedulerFactoryBean.getScheduler().triggerJob( new JobKey( jobInfo.getJobName(), jobInfo.getJobGroup() ) );
            log.info( ">>>>> jobName = [" + jobInfo.getJobName() + "]" + " scheduled and started now." );
            return true;
        } catch ( final SchedulerException e ) {
            log.error( "Failed to start new job - {}", jobInfo.getJobName(), e );
            return false;
        }
    }

    public boolean pauseJob( final SchedulerJobInfo jobInfo ) {

        try {
            final SchedulerJobInfo getJobInfo = this.schedulerRepository.findByJobName( jobInfo.getJobName() );
            getJobInfo.setJobStatus( JobStatus.PAUSED );
            this.schedulerRepository.save( getJobInfo );
            this.schedulerFactoryBean.getScheduler().pauseJob( new JobKey( jobInfo.getJobName(), jobInfo.getJobGroup() ) );
            log.info( ">>>>> jobName = [" + jobInfo.getJobName() + "]" + " paused." );
            return true;
        } catch ( final SchedulerException e ) {
            log.error( "Failed to pause job - {}", jobInfo.getJobName(), e );
            return false;
        }
    }

    public boolean resumeJob( final SchedulerJobInfo jobInfo ) {

        try {
            final SchedulerJobInfo getJobInfo = this.schedulerRepository.findByJobName( jobInfo.getJobName() );
            getJobInfo.setJobStatus( JobStatus.RESUMED );
            this.schedulerRepository.save( getJobInfo );
            this.schedulerFactoryBean.getScheduler().resumeJob( new JobKey( jobInfo.getJobName(), jobInfo.getJobGroup() ) );
            log.info( ">>>>> jobName = [" + jobInfo.getJobName() + "]" + " resumed." );
            return true;
        } catch ( final SchedulerException e ) {
            log.error( "Failed to resume job - {}", jobInfo.getJobName(), e );
            return false;
        }
    }

    public boolean deleteJob( final SchedulerJobInfo jobInfo ) {

        try {
            final SchedulerJobInfo getJobInfo = this.schedulerRepository.findByJobName( jobInfo.getJobName() );
            this.schedulerRepository.delete( getJobInfo );
            log.info( ">>>>> jobName = [" + jobInfo.getJobName() + "]" + " deleted." );
            return this.schedulerFactoryBean.getScheduler().deleteJob( new JobKey( jobInfo.getJobName(), jobInfo.getJobGroup() ) );
        } catch ( final SchedulerException e ) {
            log.error( "Failed to delete job - {}", jobInfo.getJobName(), e );
            return false;
        }
    }

    public void saveOrUpdate( final SchedulerJobInfo scheduleJob ) throws Exception {
        if ( scheduleJob.getCronExpression().length() > 0 ) {
            scheduleJob.setJobClass( SimpleCronJob.class.getName() );
            scheduleJob.setCronJob( true );
        } else {
            scheduleJob.setJobClass( SimpleJob.class.getName() );
            scheduleJob.setCronJob( false );
            scheduleJob.setRepeatTime( (long) 1 );
        }
        if ( scheduleJob.getJobId() == null ) {
            log.info( "Job Info: {}", scheduleJob );
            this.scheduleNewJob( scheduleJob );
        } else {
            this.updateScheduleJob( scheduleJob );
        }
        scheduleJob.setDescription( "i am job number " + scheduleJob.getJobId() );
        scheduleJob.setInterfaceName( "interface_" + scheduleJob.getJobId() );
        log.info( ">>>>> jobName = [" + scheduleJob.getJobName() + "]" + " created." );
    }

    @SuppressWarnings("unchecked")
    private void scheduleNewJob( final SchedulerJobInfo jobInfo ) {

        try {
            final Scheduler scheduler = this.schedulerFactoryBean.getScheduler();

            JobDetail jobDetail = JobBuilder
                    .newJob( (Class<? extends QuartzJobBean>) Class.forName( jobInfo.getJobClass() ) )
                    .withIdentity( jobInfo.getJobName(), jobInfo.getJobGroup() ).build();

            if ( !scheduler.checkExists( jobDetail.getKey() ) ) {

                jobDetail = this.scheduleCreator.createJob(
                        (Class<? extends QuartzJobBean>) Class.forName( jobInfo.getJobClass() ), false, this.context,
                        jobInfo.getJobName(), jobInfo.getJobGroup() );

                final Trigger trigger;
                if ( jobInfo.getCronJob() ) {
                    trigger = this.scheduleCreator.createCronTrigger(
                            jobInfo.getJobName(),
                            new Date(),
                            jobInfo.getCronExpression(),
                            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW );
                } else {
                    trigger = this.scheduleCreator.createSimpleTrigger(
                            jobInfo.getJobName(),
                            new Date(),
                            jobInfo.getRepeatTime(),

                            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW );
                }
                this.scheduler.scheduleJob( jobDetail, trigger );
                jobInfo.setJobStatus( JobStatus.SCHEDULED );
                this.schedulerRepository.save( jobInfo );
                log.info( ">>>>> jobName = [" + jobInfo.getJobName() + "]" + " scheduled." );
            } else {
                log.error( "scheduleNewJobRequest.jobAlreadyExist" );
            }
        } catch ( final ClassNotFoundException e ) {
            log.error( "Class Not Found - {}", jobInfo.getJobClass(), e );
        } catch ( final SchedulerException e ) {
            log.error( e.getMessage(), e );
        }
    }

    private void updateScheduleJob( final SchedulerJobInfo jobInfo ) {

        final Trigger newTrigger;

        if ( jobInfo.getCronJob() ) {

            newTrigger = this.scheduleCreator.createCronTrigger(
                    jobInfo.getJobName(),
                    new Date(),
                    jobInfo.getCronExpression(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW );
        } else {

            newTrigger = this.scheduleCreator.createSimpleTrigger(
                    jobInfo.getJobName(),
                    new Date(),
                    jobInfo.getRepeatTime(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW );
        }
        try {
            this.schedulerFactoryBean.getScheduler().rescheduleJob( TriggerKey.triggerKey( jobInfo.getJobName() ), newTrigger );
            jobInfo.setJobStatus( JobStatus.UPDATED );
            this.schedulerRepository.save( jobInfo );
            log.info( ">>>>> jobName = [" + jobInfo.getJobName() + "]" + " updated and scheduled." );
        } catch ( final SchedulerException e ) {
            log.error( e.getMessage(), e );
        }
    }

    public List<SchedulerJobInfo> getAllJobs() {
        return this.schedulerRepository.findAll();
    }

    public SchedulerMetaData getMetaData() throws SchedulerException {
        return this.scheduler.getMetaData();
    }

}
