package com.charter.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {
    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job processZipFileJob(Step downloadStep) {
        return new JobBuilder("processZipFileJob", jobRepository)
                .start(downloadStep)
                .build();
    }
    @Bean
    public Step downloadFileStep(Tasklet downloadFileTasklet) {
        return new StepBuilder("downloadFileStep", jobRepository)
                .tasklet(downloadFileTasklet, transactionManager)
                .build();
    }
}
