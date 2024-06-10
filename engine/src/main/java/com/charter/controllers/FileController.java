package com.charter.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final JobLauncher jobLauncher;

    private final Job processZipFileJob;

    /**
     * Starts the batch job to download a ZIP file.
     * <p>
     * This endpoint triggers the Spring Batch job to start processing. It accepts a URL
     * path as a request parameter, which specifies the location of the ZIP file to be downloaded.
     * If the job starts successfully, it returns a success message; otherwise, it returns an error message.
     * </p>
     *
     * @param urlPath the URL path of the ZIP file to be downloaded.
     * @return a ResponseEntity with a message indicating the job start status.
     */
    @GetMapping("/startDownload")
    public ResponseEntity<String> startJob(@RequestParam("urlPath") String urlPath) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("urlPath", urlPath)
                    .toJobParameters();
            JobExecution execution = jobLauncher.run(processZipFileJob, jobParameters);
            if(BatchStatus.FAILED == execution.getStatus()) {
                String err = execution.getExitStatus().getExitDescription();
                return ResponseEntity.status(500).body("Job failed: " + err.split("\\n")[0]);
            } else {
                return ResponseEntity.ok("Job completed successfully.");
            }
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.error("Job failed: " + e.getMessage(), e);
            return ResponseEntity.status(500).body("Job failed: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred: " + e.getMessage(), e);
            return ResponseEntity.status(500).body("Unexpected error occurred: " + e.getMessage());
        }
    }
}
