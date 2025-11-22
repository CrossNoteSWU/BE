package com.swulion.crossnote.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.build();

        List<HttpMessageConverter<?>> converters = new ArrayList<>(restTemplate.getMessageConverters());

        // JSON 변환기 추가
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter jackson) {

                List<MediaType> mediaTypes = new ArrayList<>(jackson.getSupportedMediaTypes());
                mediaTypes.add(MediaType.valueOf("text/json"));
                mediaTypes.add(MediaType.valueOf("text/json;charset=UTF-8"));

                jackson.setSupportedMediaTypes(mediaTypes);
            }
        }

        // XML 변환기 추가
        converters.add(new Jaxb2RootElementHttpMessageConverter());
        restTemplate.setMessageConverters(converters);

        return restTemplate;
    }
}
