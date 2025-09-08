package com.research.assistant.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gemini.api")
@Getter
@Setter
@ToString(exclude = "key")
public class GeminiApiProperties {
  private String url;
  private String key;
}
