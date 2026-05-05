package com.echoofmemories.project.controller;

import com.echoofmemories.project.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("ok", "ok");
    }
}
