package br.com.asv.service.configuration;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public interface ISwaggerConfiguration {

	OpenAPI configurarionSwagger(SwaggerConfigurationData swaggerConfigurationData);
}
