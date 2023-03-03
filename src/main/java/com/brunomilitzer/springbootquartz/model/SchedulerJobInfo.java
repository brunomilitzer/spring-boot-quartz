package com.brunomilitzer.springbootquartz.model;

import com.brunomilitzer.springbootquartz.enumeration.JobStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@Table(name = "scheduler_job_info")
public class SchedulerJobInfo implements Serializable {

    protected String cronExpression;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long jobId;

    private String jobName;

    private String jobGroup;

    private JobStatus jobStatus;

    private String jobClass;

    private String description;

    private String interfaceName;

    private Long repeatTime;

    private Boolean cronJob;

}
