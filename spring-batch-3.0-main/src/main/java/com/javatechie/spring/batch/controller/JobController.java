package com.javatechie.spring.batch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    private final String TEMP_STORAGE = "C:\\LoadPanel\\Notes\\batch\\";

    @PostMapping("/importCustomers")
    public void importCsvToDBJob(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        File f = new File(TEMP_STORAGE + fileName);
        file.transferTo(f);
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", TEMP_STORAGE + fileName)
                .addLong("startAt", System.currentTimeMillis()).toJobParameters();
        try {
            JobExecution execution = jobLauncher.run(job, jobParameters);

            if(execution.getExitStatus().getExitCode().equals(ExitStatus.COMPLETED)){
                //delete the file from the TEMP_STORAGE
                Files.deleteIfExists(Paths.get(TEMP_STORAGE + fileName));
            }
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }
}
