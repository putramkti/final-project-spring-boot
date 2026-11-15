package com.bootcamp19.scheduler_service.service.scheduler;

import com.bootcamp19.scheduler_service.service.ProductImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductImportScheduler implements ApplicationRunner {
    private final ProductImportService productImportService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("[Scheduler] Executing INITIAL STARTUP import...");
        productImportService.executeImport("APPLICATION_STARTUP");
    }

    @Scheduled(cron = "${scheduler.cron}")
    public void runScheduledImport() {
        log.info("[Scheduler] Executing SCHEDULED CRON import...");
        productImportService.executeImport("CRON_SCHEDULE");
    }
}
