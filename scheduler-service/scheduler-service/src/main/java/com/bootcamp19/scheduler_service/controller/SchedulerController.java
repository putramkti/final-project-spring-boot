package com.bootcamp19.scheduler_service.controller;

import com.bootcamp19.scheduler_service.dto.response.BaseResponse;
import com.bootcamp19.scheduler_service.rest.ProductServiceClient;
import com.bootcamp19.scheduler_service.service.ProductImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SchedulerController {
    private final ProductImportService productImportService;

    @PostMapping("/import")
    public BaseResponse<Object> triggerManualImport(){
        log.info("[Scheduler] Manual import triggered via REST API");
        productImportService.executeImport("MANUAL_REST_TRIGGER");

        return BaseResponse.ok("Manual import triggered successfully", null);
    }
}
