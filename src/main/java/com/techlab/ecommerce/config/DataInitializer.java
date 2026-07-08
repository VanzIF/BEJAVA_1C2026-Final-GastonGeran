package com.techlab.ecommerce.config;

import com.techlab.ecommerce.model.Category;
import com.techlab.ecommerce.model.Product;
import com.techlab.ecommerce.repository.CategoryRepository;
import com.techlab.ecommerce.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// carga algunos datos de prueba al arrancar, solo si la base esta vacia
@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataInitializer(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }

        Category electronics = categoryRepository.save(new Category("Electronica", "Dispositivos y accesorios electronicos"));
        Category books = categoryRepository.save(new Category("Libros", "Libros fisicos y digitales"));
        Category home = categoryRepository.save(new Category("Hogar", "Articulos para el hogar"));

        productRepository.save(new Product("Auriculares Bluetooth", "Auriculares inalambricos con cancelacion de ruido",
                new BigDecimal("45000.00"), 25, electronics));
        productRepository.save(new Product("Teclado mecanico", "Teclado mecanico retroiluminado",
                new BigDecimal("68000.00"), 15, electronics));
        productRepository.save(new Product("Clean Code", "Libro sobre buenas practicas de programacion",
                new BigDecimal("22000.00"), 30, books));
        productRepository.save(new Product("Effective Java", "Libro de referencia sobre Java",
                new BigDecimal("25000.00"), 20, books));
        productRepository.save(new Product("Set de sartenes", "Juego de 3 sartenes antiadherentes",
                new BigDecimal("30000.00"), 10, home));
    }
}
