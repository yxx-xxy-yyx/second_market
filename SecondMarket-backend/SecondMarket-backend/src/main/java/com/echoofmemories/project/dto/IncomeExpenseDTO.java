package com.echoofmemories.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 收入支出统计DTO
 * 
 * @author EchoOfMemories
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeExpenseDTO {
    
    private String date;
    
    private BigDecimal amount;
}

