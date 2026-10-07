package com.example.sim_registration.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "nida")
public class NidaProperties {

    private boolean mock = true;

    private String baseUrl;

    private String clientId;

    private String clientSecret;

    private int connectTimeoutSeconds = 5;

    private int readTimeoutSeconds = 10;
}
