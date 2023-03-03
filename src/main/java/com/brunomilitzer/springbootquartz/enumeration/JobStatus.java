package com.brunomilitzer.springbootquartz.enumeration;

import lombok.Getter;

@Getter
public enum JobStatus {

    STARTED,
    PAUSED,
    RESUMED,
    SCHEDULED,
    UPDATED,
}
