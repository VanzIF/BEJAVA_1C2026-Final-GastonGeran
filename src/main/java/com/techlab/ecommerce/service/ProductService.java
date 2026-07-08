package com.techlab.ecommerce.service;

import com.techlab.ecommerce.exception.ResourceNotFoundException;
import com.techlab.ecommerce.model.Category;
import com.techlab.ecommerce.model.Product;
import com.techlab.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el producto con id " + id));
    }

    public List<Product> findByCategory(Long categoryId) {
        // valida que la categoria exista antes de filtrar
        categoryService.findById(categoryId);
        return productRepository.findByCategoryId(categoryId);
    }

    public Product create(Product product) {
        Category category = categoryService.findById(product.getCategory().getId());
        product.setCategory(category);
        return productRepository.save(product);
    }

    public Product update(Long id, Product updated) {
        Product product = findById(id);
        Category category = categoryService.findById(updated.getCategory().getId());

        product.setName(updated.getName());
        product.setDescription(updated.getDescription());
        product.setPrice(updated.getPrice());
        product.setStock(updated.getStock());
        product.setCategory(category);
        return productRepository.save(product);
    }

    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }
}
