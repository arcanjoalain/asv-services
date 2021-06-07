package br.com.asv.service.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

public abstract class ASwaggerConfiguration implements ISwaggerConfiguration {

	@Override
	public OpenAPI securityOpenAPI(SwaggerConfigurationData swaggerConfigurationData) {
		return new OpenAPI()
				.info(new Info().title(swaggerConfigurationData.getAppName())
						.version(swaggerConfigurationData.getAppVersion())
						.description(swaggerConfigurationData.getAppDescription())
						.termsOfService(swaggerConfigurationData.getUrlTerm())
						.license(new License().name(swaggerConfigurationData.getLicenseType())
								.url(swaggerConfigurationData.getUrlLicense())))
				.components(new Components().addSecuritySchemes("bearer-jwt",
						new SecurityScheme().type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER)
								.name(swaggerConfigurationData.getHeaderName()))
				).addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
	}

	@Override
	public OpenAPI simpleOpenAPI(SwaggerConfigurationData swaggerConfigurationData) {
		return new OpenAPI().info(new Info().title(swaggerConfigurationData.getAppDescription() + " application API")
				.version(swaggerConfigurationData.getAppVersion())
				.description(swaggerConfigurationData.getAppDescription())
				.termsOfService(swaggerConfigurationData.getUrlTerm())
				.license(new License().name(swaggerConfigurationData.getLicenseType())
						.url(swaggerConfigurationData.getUrlLicense())));
	}
}
