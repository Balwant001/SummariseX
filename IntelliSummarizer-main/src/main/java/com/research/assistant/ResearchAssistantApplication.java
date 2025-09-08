package com.research.assistant;

import com.research.assistant.config.GeminiApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(GeminiApiProperties.class)
public class ResearchAssistantApplication {

  public static void main(String[] args) {
    SpringApplication.run(ResearchAssistantApplication.class, args);
  }
}
