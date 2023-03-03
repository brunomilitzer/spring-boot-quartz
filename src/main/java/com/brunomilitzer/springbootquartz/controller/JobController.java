package com.brunomilitzer.springbootquartz.controller;

import com.brunomilitzer.springbootquartz.model.Message;
import com.brunomilitzer.springbootquartz.model.SchedulerJobInfo;
import com.brunomilitzer.springbootquartz.service.SchedulerJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api")
public class JobController {

    private final SchedulerJobService jobService;

    public JobController( final SchedulerJobService jobService ) {
        this.jobService = jobService;
    }

    @RequestMapping(value = "/saveOrUpdate", method = { RequestMethod.GET, RequestMethod.POST })
    public Object saveOrUpdate( final SchedulerJobInfo job ) {

        log.info( "params, job = {}", job );
        Message message = Message.failure();

        try {
            this.jobService.saveOrUpdate( job );
            message = Message.success();

        } catch ( final Exception e ) {
            message.setMsg( e.getMessage() );
            log.error( "updateCron ex:", e );
        }
        return message;
    }

    @RequestMapping("/metaData")
    public Object metaData() throws SchedulerException {
        final SchedulerMetaData metaData = this.jobService.getMetaData();
        return metaData;
    }

    @RequestMapping("/getAllJobs")
    public Object getAllJobs() throws SchedulerException {
        return this.jobService.getAllJobs();
    }

    @RequestMapping(value = "/runJob", method = { RequestMethod.GET, RequestMethod.POST })
    public Object runJob( final SchedulerJobInfo job ) {

        log.info( "params, job = {}", job );
        Message message = Message.failure();

        try {
            this.jobService.startJobNow( job );
            message = Message.success();

        } catch ( final Exception e ) {
            message.setMsg( e.getMessage() );
            log.error( "runJob ex:", e );
        }

        return message;
    }

    @RequestMapping(value = "/pauseJob", method = { RequestMethod.GET, RequestMethod.POST })
    public Object pauseJob( final SchedulerJobInfo job ) {

        log.info( "params, job = {}", job );
        Message message = Message.failure();

        try {
            this.jobService.pauseJob( job );
            message = Message.success();

        } catch ( final Exception e ) {
            message.setMsg( e.getMessage() );
            log.error( "pauseJob ex:", e );
        }

        return message;
    }

    @RequestMapping(value = "/resumeJob", method = { RequestMethod.GET, RequestMethod.POST })
    public Object resumeJob( final SchedulerJobInfo job ) {

        log.info( "params, job = {}", job );
        Message message = Message.failure();

        try {
            this.jobService.resumeJob( job );
            message = Message.success();

        } catch ( final Exception e ) {
            message.setMsg( e.getMessage() );
            log.error( "resumeJob ex:", e );
        }

        return message;
    }

    @RequestMapping(value = "/deleteJob", method = { RequestMethod.GET, RequestMethod.POST })
    public Object deleteJob( final SchedulerJobInfo job ) {

        log.info( "params, job = {}", job );
        Message message = Message.failure();

        try {
            this.jobService.deleteJob( job );
            message = Message.success();

        } catch ( final Exception e ) {
            message.setMsg( e.getMessage() );
            log.error( "deleteJob ex:", e );
        }

        return message;
    }

}