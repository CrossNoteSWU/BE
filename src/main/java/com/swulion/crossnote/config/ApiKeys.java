package com.swulion.crossnote.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "api.key")
@Getter
@Setter
public class ApiKeys {
    private Naver naver = new Naver();

    private String youtube;

    private String nationalLib;
    private String dbpia;

    private String kci;

    private String gemini;

    @Getter
    @Setter
    public static class Naver {
        private String clientId;
        private String clientSecret;
    }
}