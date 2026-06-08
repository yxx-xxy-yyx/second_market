package com.echoofmemories.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiSmartSearchResponse {
    private String interpretedQuery;
    private List<String> suggestedTags;
    private List<Map<String, Object>> products;
    private List<Map<String, Object>> similarSearches;
    private String searchTip;
    private Integer totalCount;
}
