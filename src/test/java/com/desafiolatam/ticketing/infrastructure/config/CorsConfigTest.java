package com.desafiolatam.ticketing.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("CorsConfig Tests")
class CorsConfigTest {

    @Test
    @DisplayName("Should configure CORS mappings for all routes")
    void shouldConfigureCorsMappings() {
        // Arrange
        CorsConfig corsConfig = new CorsConfig();
        CorsRegistry registry = mock(CorsRegistry.class);
        CorsRegistration registration = mock(CorsRegistration.class, RETURNS_DEEP_STUBS);

        when(registry.addMapping(anyString())).thenReturn(registration);

        // Act
        corsConfig.addCorsMappings(registry);

        // Assert
        verify(registry, times(1)).addMapping("/**");
        assertNotNull(corsConfig);
    }
}
