package com.echoofmemories.project.dto;

import lombok.Data;

@Data
public class AiCampusServiceRequest {
    private String serviceType;
    private String request;
    private String additionalInfo;
}
