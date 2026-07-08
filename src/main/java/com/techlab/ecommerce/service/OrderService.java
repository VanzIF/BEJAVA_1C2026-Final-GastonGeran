package com.techlab.ecommerce.service;

import com.techlab.ecommerce.dto.OrderRequestDTO;
import com.techlab.ecommerce.exception.InsufficientStockException;
import com.techlab.ecommerce.exception.InvalidOrderException;
import com.techlab.ecommerce.exception.ResourceNotFoundException;
import com.techlab.ecommerce.model.Order;
import com.techlab.ecommerce.model.OrderStatus;
import com.techlab.ecommerce.model.Product;
import com.techlab.ecommerce.repository.OrderRepository;
import com.techlab.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro la orden con id " + id));
    }

    // valida que la orden tenga productos, que existan y que haya stock,
    // y descuenta el stock antes de guardar. si mandan el mismo id repetido
    // lo tomo como cantidad (ej: [1,1,3] = 2 unidades del producto 1 y 1 del 3)
    @Transactional
    public Order create(OrderRequestDTO request) {
        if (request.getProductIds() == null || request.getProductIds().isEmpty()) {
            throw new InvalidOrderException("La orden debe contener al menos un producto");
        }

        // agrupo por id para saber cuantas unidades de cada producto piden
        Map<Long, Long> quantitiesById = new HashMap<>();
        for (Long productId : request.getProductIds()) {
            quantitiesById.merge(productId, 1L, Long::sum);
        }

        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Long> entry : quantitiesById.entrySet()) {
            Long productId = entry.getKey();
            Long quantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontro el producto con id " + productId));

            if (product.getStock() < quantity) {
                throw new InsufficientStockException(
                        "Stock insuficiente para el producto '" + product.getName() + "'. "
                                + "Disponible: " + product.getStock() + ", solicitado: " + quantity);
            }

            product.setStock((int) (product.getStock() - quantity));
            productRepository.save(product);

            order.getProducts().add(product);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        order.setTotal(total);
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateStatus(Long id, OrderStatus status) {
        Order order = findById(id);

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderException("No se puede modificar una orden cancelada");
        }

        // repongo 1 unidad por cada producto asociado al cancelar (no guardo
        // la cantidad exacta por producto, para eso habria que usar una tabla intermedia propia)
        if (status == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
            for (Product product : order.getProducts()) {
                product.setStock(product.getStock() + 1);
                productRepository.save(product);
            }
        }

        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void delete(Long id) {
        Order order = findById(id);
        orderRepository.delete(order);
    }
}
