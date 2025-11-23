package com.swulion.crossnote.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

// JSON 응답을 강제하기 위한 설정 DTO
@Getter
public class GenerationConfig {

    @JsonProperty("response_mime_type")
    private String responseMimeType;

    public GenerationConfig() {
        this.responseMimeType = "application/json";
    }
}
