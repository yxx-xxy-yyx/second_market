package com.echoofmemories.project.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class AiAuthenticationRequest {
    @NotEmpty(message = "图片列表不能为空")
    private List<String> imageUrls;
    private String productName;
    private String brand;
    private String model;
    private String category;
    private String additionalInfo;
}
