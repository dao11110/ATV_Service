package com.amkor.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ReportService {

    @Async("appAsyncExecutor")
    public CompletableFuture<String> generateReport(String method) {
        // heavy work
        String result = "Report#" + method + " done on " + Thread.currentThread().getName();
        return CompletableFuture.completedFuture(result);
    }


}
