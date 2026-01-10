package com.miko.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "volc.ark")
public class VolcArkConfig {
//    public String apiKey;
//    public String baseUrl;
    public String model;
    public List<String> models;
}
