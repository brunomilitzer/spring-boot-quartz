package com.brunomilitzer.springbootquartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.stream.IntStream;

@Slf4j
public class SimpleJob extends QuartzJobBean {

    @Override
    protected void executeInternal( final JobExecutionContext context ) throws JobExecutionException {
        log.info( "SimpleJob Start..........." );

        IntStream.range( 0, 5 ).forEach( i -> {
            log.info( "Counting - {}", i );

            try {
                Thread.sleep( 1000 );
            } catch ( final InterruptedException e ) {
                log.error( e.getMessage(), e );
            }
        } );

        log.info( "SimpleJob End..........." );
    }

}
