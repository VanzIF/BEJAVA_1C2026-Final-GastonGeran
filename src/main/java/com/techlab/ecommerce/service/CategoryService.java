package com.techlab.ecommerce.service;

import com.techlab.ecommerce.exception.DuplicateResourceException;
import com.techlab.ecommerce.exception.ResourceNotFoundException;
import com.techlab.ecommerce.model.Category;
import com.techlab.ecommerce.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro la categoria con id " + id));
    }

    public Category create(Category category) {
        categoryRepository.findByNameIgnoreCase(category.getName()).ifPresent(existing -> {
            throw new DuplicateResourceException("Ya existe una categoria con el nombre '" + category.getName() + "'");
        });
        return categoryRepository.save(category);
    }

    public Category update(Long id, Category updated) {
        Category category = findById(id);
        category.setName(updated.getName());
        category.setDescription(updated.getDescription());
        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        Category category = findById(id);
        categoryRepository.delete(category);
    }
}
