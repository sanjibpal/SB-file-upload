package com.charter.controllers;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FileControllerTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job processZipFileJob;

    @InjectMocks
    private FileController fileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStartJob_Success() throws Exception {
        JobExecution jobExecution = mock(JobExecution.class);
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);
        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(jobExecution);

        ResponseEntity<String> response = fileController.startJob("http://example.com/file.zip");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Job completed successfully.", response.getBody());
    }


    @Test
    void testStartJob_Failure() throws Exception {
        JobExecution jobExecution = mock(JobExecution.class);
        when(jobExecution.getExitStatus()).thenReturn(new ExitStatus("FAILED","Job failed"));
        when(jobExecution.getStatus()).thenReturn(BatchStatus.FAILED);
        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(jobExecution);

        ResponseEntity<String> response = fileController.startJob("http://example.com/file.zip");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Job failed: Job failed", response.getBody());
    }

    /*
    @Test
    void testStartJob_JobExecutionAlreadyRunningException() throws Exception {
        doThrow(JobExecutionAlreadyRunningException.class).when(jobLauncher).run(any(Job.class), any(JobParameters.class));

        ResponseEntity<String> response = fileController.startJob("http://example.com/file.zip");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Job failed: org.springframework.batch.core.repository.JobExecutionAlreadyRunningException", response.getBody());
    }
    */
}
