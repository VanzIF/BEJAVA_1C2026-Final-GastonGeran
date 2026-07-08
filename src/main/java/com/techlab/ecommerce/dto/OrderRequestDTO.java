package com.techlab.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class OrderRequestDTO {

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String customerName;

    @NotBlank(message = "El email del cliente es obligatorio")
    @Email(message = "El email debe tener un formato valido")
    private String customerEmail;

    @NotEmpty(message = "La orden debe contener al menos un producto")
    private List<Long> productIds;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }
}
