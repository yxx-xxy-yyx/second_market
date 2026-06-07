package com.echoofmemories.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiCampusMatchResponse {
    private List<Map<String, Object>> matchedBuyers;
    private List<Map<String, Object>> matchedSellers;
    private String meetupSuggestions;
    private String safetyTips;
}
