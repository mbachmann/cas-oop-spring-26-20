
package com.example.demoinitial.config;

import java.util.List;

import com.example.demoinitial.utils.HasLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS configuration: CORS must be processed before Spring Security because the pre-flight request will not contain any cookies.
 * Therefore, the request would determine the user is not authenticated
 * and reject it.
 */
@Configuration
public class CorsConfig implements HasLogger {

    @Value("${endpoints.web.cors.path-mappings}")
    private String pathMappings;
    @Value("${endpoints.web.cors.allowed-origins}")
    private List<String> allowedOrigins;
    @Value("${endpoints.web.cors.allowed-methods}")
    private List<String> allowedMethods;
    @Value("${endpoints.web.cors.allowed-headers}")
    private List<String> allowedHeaders;


    /**
     * This Option need does NOT NEED an explicit call to the  http.cors().and() method in WebSecurityConfig
     */
    @Bean
    public CorsFilter corsFilter() {
        getLogger().info("================================");
        getLogger().info("Initializing CORS Configuration");
        getLogger().info("================================");
        getLogger().info("Path Mappings: {}", pathMappings);
        getLogger().info("Allowed Origins: {}", allowedOrigins);
        getLogger().info("Allowed Methods: {}", allowedMethods);
        getLogger().info("Allowed Headers: {}", allowedHeaders);
        getLogger().info("Max Age: 60 seconds");
        getLogger().info("Allow Credentials: true");
        getLogger().info("================================");

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(allowedMethods);
        config.setAllowedHeaders(allowedHeaders);
        config.setAllowCredentials(true);
        config.setMaxAge(60L);
        source.registerCorsConfiguration(pathMappings, config);
        return new CorsFilter(source);
    }

    /**
     * This Option need an explicit call to the  http.cors().and() method in WebSecurityConfig
     * @return
     */
    /* @Bean
    CorsConfigurationSource corsConfigurationSource()  {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(pathMappings, configuration);
        return source;
    }*/

}
