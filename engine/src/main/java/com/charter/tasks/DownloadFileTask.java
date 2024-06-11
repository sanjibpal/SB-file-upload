package com.charter.tasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@RequiredArgsConstructor
@Component
public class DownloadFileTask implements Tasklet {
    private final RetryTemplate retryTemplate;

    @Value("${app.file.download.path}")
    private String fileDownloadPath;

    /**
     * Resume the Download process if interrupted.
     * @param contribution mutable state to be passed back to update the current step
     * execution
     * @param chunkContext attributes shared between invocations but not between restarts
     * @return Status
     * @throws IOException
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws IOException {
        String urlPath = chunkContext.getStepContext().getJobParameters().get("urlPath").toString();
        Assert.hasLength(urlPath, "Url path is empty.");
        retryTemplate.execute(context -> {
            try {
                download(urlPath, fileDownloadPath);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
                // No need retrying
                context.setExhaustedOnly();
                throw e;
            } catch (IOException e) {
                log.error(e.getMessage());
                throw e;
            }
            return null;
        });
        return RepeatStatus.FINISHED;
    }

    /**
     * Downloads the file with the ability to resume the download if interrupted.
     * @param urlPath download url
     * @throws IOException FileNotFoundException will stop the retry
     */
    public void download(String urlPath, String fileDownloadPath) throws IOException{
        URL url = new URL(urlPath);
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
        } catch(FileNotFoundException e) {
            log.error("Download Fail: {} with error {}", urlPath, e);
            throw new FileNotFoundException("Download Fail: " + urlPath);
        }

        int responseCode = httpURLConnection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_PARTIAL && responseCode != HttpURLConnection.HTTP_OK) {
            httpURLConnection.disconnect();
            log.error("Failed to download {} - {}", urlPath, responseCode);
            throw new FileNotFoundException("Failed to download with response code " + responseCode);
        }

        try(RandomAccessFile file = new RandomAccessFile(fileDownloadPath, "rw")) {
            long fileSize = 0;
            long bytesReadCount = 0; // display console status
            if (file.length() > 0) {
                fileSize = file.length();
                httpURLConnection.setRequestProperty("Range", "bytes=" + fileSize + "-");
                file.seek(fileSize);
            }

            try (InputStream inputStream = httpURLConnection.getInputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    file.write(buffer, 0, bytesRead);
                }
                bytesReadCount+=4096;
                if(fileSize<=bytesReadCount) {
                    log.info("Final Bytes read {} of {}", (bytesReadCount-fileSize), fileSize);
                } else {
                    log.info("Bytes read {} of {}", bytesReadCount, fileSize);
                }
            } finally {
                httpURLConnection.disconnect();
            }
        }
    }
}
