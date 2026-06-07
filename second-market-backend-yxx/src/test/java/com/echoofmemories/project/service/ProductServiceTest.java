package com.echoofmemories.project.service;

import com.echoofmemories.project.BaseTest;
import com.echoofmemories.project.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceTest extends BaseTest {

    @Autowired
    private ProductService productService;

    @Test
    public void testGetProductById() {
        Product product = productService.getById(1L);
        System.out.println("查询到的商品: " + product);
        assertNotNull(product);
    }
}
