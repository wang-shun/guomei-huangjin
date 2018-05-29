package com.gomemyc.exception;

import com.dangdang.ddframe.job.executor.handler.JobExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wuhui on 2016/12/15.
 */
public class MycJobExceptionHandler implements JobExceptionHandler {

	
	
	
    private static final Logger logger = LoggerFactory.getLogger(MycJobExceptionHandler.class);

    @Override
    public void handleException(String jobName, Throwable cause) {
        logger.error("jobName={},exception={}",jobName,cause);
    }
}
