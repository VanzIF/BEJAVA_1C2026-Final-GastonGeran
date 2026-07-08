package com.techlab.ecommerce.dto;

import com.techlab.ecommerce.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class OrderStatusUpdateDTO {

    @NotNull(message = "El estado es obligatorio")
    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
