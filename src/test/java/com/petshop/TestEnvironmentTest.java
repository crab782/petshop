package com.petshop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TestEnvironmentTest extends BaseTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext, "Application context should not be null");
    }

    @Test
    void testProfileIsActive() {
        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        boolean hasTestProfile = false;
        for (String profile : activeProfiles) {
            if ("test".equals(profile)) {
                hasTestProfile = true;
                break;
            }
        }
        assert hasTestProfile : "Test profile should be active";
    }
}
