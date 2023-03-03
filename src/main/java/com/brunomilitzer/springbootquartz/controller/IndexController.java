package com.brunomilitzer.springbootquartz.controller;

import com.brunomilitzer.springbootquartz.model.SchedulerJobInfo;
import com.brunomilitzer.springbootquartz.service.SchedulerJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {

    private final SchedulerJobService jobService;

    @Autowired
    public IndexController( final SchedulerJobService jobService ) {
        this.jobService = jobService;
    }

    @GetMapping("/index")
    public String index( final Model model ) {
        final List<SchedulerJobInfo> jobs = this.jobService.getAllJobs();
        model.addAttribute( "jobs", jobs );

        return "index";
    }

}
