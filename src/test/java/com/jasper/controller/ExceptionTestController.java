package com.jasper.controller;

import com.jasper.common.CommonResponse;
import org.springframework.web.bind.annotation.*;

/**
 * Test Controller for Exception Handling Tests
 * Only used in test environment to trigger exceptions
 */
@RestController
@RequestMapping("/api/test/exceptions")
public class ExceptionTestController {

    /**
     * Trigger IllegalArgumentException
     */
    @PostMapping("/illegal-argument")
    public CommonResponse<Void> triggerIllegalArgumentException(@RequestParam String message) {
        throw new IllegalArgumentException(message);
    }

    /**
     * Trigger RuntimeException
     */
    @PostMapping("/runtime")
    public CommonResponse<Void> triggerRuntimeException(@RequestParam String message) {
        throw new RuntimeException(message);
    }

    /**
     * Trigger general Exception (wrapped in RuntimeException)
     */
    @PostMapping("/general")
    public CommonResponse<Void> triggerGeneralException(@RequestParam String message) {
        throw new RuntimeException(message);
    }
}
