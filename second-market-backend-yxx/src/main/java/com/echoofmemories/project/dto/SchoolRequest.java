package com.echoofmemories.project.dto;

import lombok.Data;

@Data
public class SchoolRequest {

    private Long id;

    private String schoolCode;

    private String nameZh;

    private String nameKo;

    private String nameEn;

    private String cityZh;

    private String cityKo;

    private String cityEn;

    private String logoUrl;

    private Integer status;
}
