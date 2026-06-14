package com.example.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOrderRequest {
    
    private String status;
    
    private String remark;
    
    private String receiverName;
    
    private String receiverPhone;
    
    private String receiverAddress;
}
