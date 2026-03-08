package com.flprocha.ecommerce_api;

import com.flprocha.ecommerce_api.dto.request.ProductRequest;
import com.flprocha.ecommerce_api.dto.response.ProductResponse;
import com.flprocha.ecommerce_api.entity.Product;
import com.flprocha.ecommerce_api.exception.InsufficientStockException;
import com.flprocha.ecommerce_api.exception.ResourceNotFoundException;
import com.flprocha.ecommerce_api.repository.ProductRepository;
import com.flprocha.ecommerce_api.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void shouldCreateProductSuccessfully() {
        // Arrange
        var request = ProductRequest.builder()
                .name("Notebook Dell")
                .description("Notebook Dell Inspiron 15")
                .price(new BigDecimal("3499.99"))
                .stockQuantity(10)
                .build();

        var savedProduct = Product.builder()
                .id(1L)
                .name("Notebook Dell")
                .description("Notebook Dell Inspiron 15")
                .price(new BigDecimal("3499.99"))
                .stockQuantity(10)
                .active(true)
                .build();

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        // Act
        ProductResponse response = productService.create(request);

        // Assert
        assertNotNull(response);
        assertEquals("Notebook Dell", response.getName());
        assertEquals(new BigDecimal("3499.99"), response.getPrice());
        assertEquals(10, response.getStockQuantity());
        assertTrue(response.isActive());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void shouldFindProductByIdSuccessfully() {
        // Arrange
        var product = Product.builder()
                .id(1L)
                .name("Mouse Gamer")
                .price(new BigDecimal("299.99"))
                .stockQuantity(5)
                .active(true)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        // Act
        ProductResponse response = productService.findById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Mouse Gamer", response.getName());
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado")
    void shouldThrowExceptionWhenProductNotFound() {
        // Arrange
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> productService.findById(99L));

        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve diminuir estoque com sucesso")
    void shouldDecreaseStockSuccessfully() {
        // Arrange
        var product = Product.builder()
                .id(1L)
                .name("Teclado")
                .stockQuantity(10)
                .active(true)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        // Act
        productService.decreaseStock(1L, 3);

        // Assert
        assertEquals(7, product.getStockQuantity());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("Deve lançar exceção com estoque insuficiente")
    void shouldThrowWhenInsufficientStock() {
        // Arrange
        var product = Product.builder()
                .id(1L)
                .name("TV 4K")
                .stockQuantity(2)
                .active(true)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(InsufficientStockException.class,
                () -> productService.decreaseStock(1L, 5));

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve desativar produto com sucesso")
    void shouldDeactivateProductSuccessfully() {
        // Arrange
        var product = Product.builder()
                .id(1L)
                .name("Produto Teste")
                .stockQuantity(5)
                .active(true)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        // Act
        productService.deactivate(1L);

        // Assert
        assertFalse(product.isActive());
        verify(productRepository, times(1)).save(product);
    }
}