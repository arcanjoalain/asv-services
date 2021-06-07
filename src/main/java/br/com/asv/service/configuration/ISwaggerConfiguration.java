package br.com.asv.service.configuration;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
/**
 * 
 * @author alain.vieira
 *
 */
@Configuration
public interface ISwaggerConfiguration {

	OpenAPI configurarionSwagger(SwaggerConfigurationData swaggerConfigurationData);
	
	OpenAPI securityOpenAPI(SwaggerConfigurationData swaggerConfigurationData);
	
	OpenAPI simpleOpenAPI(SwaggerConfigurationData swaggerConfigurationData);
}
