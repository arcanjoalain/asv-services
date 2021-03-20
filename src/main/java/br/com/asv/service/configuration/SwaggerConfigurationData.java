package br.com.asv.service.configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwaggerConfigurationData {
	private String keyType = "bearer-jwt";
	private String appName; 
	private String appDescription; 
	private String appVersion;
	private String urlTerm;
	private String licenseType;
	private String urlLicense;
	private String headerName;
}
