package com.techlab.ecommerce.controller;

import com.techlab.ecommerce.dto.OrderRequestDTO;
import com.techlab.ecommerce.dto.OrderStatusUpdateDTO;
import com.techlab.ecommerce.model.Order;
import com.techlab.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public Order getById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@Valid @RequestBody OrderRequestDTO request) {
        return orderService.create(request);
    }

    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusUpdateDTO request) {
        return orderService.updateStatus(id, request.getStatus());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
