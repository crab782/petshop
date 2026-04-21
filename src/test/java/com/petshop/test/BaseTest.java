package com.petshop.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class BaseTest {

    @BeforeEach
    public void setUp() {
        // 测试前的准备工作
        System.out.println("Setting up test...");
    }

    @AfterEach
    public void tearDown() {
        // 测试后的清理工作
        System.out.println("Tearing down test...");
    }

    // 提供通用的测试方法
    protected void log(String message) {
        System.out.println("Test Log: " + message);
    }

    protected void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
