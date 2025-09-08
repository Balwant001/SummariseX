package com.research.assistant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.research.assistant.config.GeminiApiProperties;
import com.research.assistant.dto.GeminiResponse;
import com.research.assistant.dto.ResearchRequest;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ResearchService {
  private final WebClient webClient;
  private final ObjectMapper objectMapper;
  private final GeminiApiProperties geminiApiProperties;

  public ResearchService(
      WebClient.Builder webClientBuilder,
      ObjectMapper objectMapper,
      GeminiApiProperties geminiApiProperties) {
    this.webClient = webClientBuilder.build();
    this.objectMapper = objectMapper;
    this.geminiApiProperties = geminiApiProperties;
  }

  public String processContent(ResearchRequest request) {
    // Build the prompt
    String prompt = buildPrompt(request);
    String baseUrl = geminiApiProperties.getUrl();

    // Query the AI Model API
    Map<String, Object> requestBody =
        Map.of("contents", new Object[] {Map.of("parts", new Object[] {Map.of("text", prompt)})});

    String response =
        webClient
            .post()
            .uri(baseUrl + geminiApiProperties.getKey())
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String.class)
            .block();

    return extractTextFromResponse(response);
  }

  private String extractTextFromResponse(String response) {
    try {
      GeminiResponse geminiResponse = objectMapper.readValue(response, GeminiResponse.class);
      if (geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()) {
        GeminiResponse.Candidate firstCandidate = geminiResponse.getCandidates().get(0);
        if (firstCandidate.getContent() != null
            && firstCandidate.getContent().getParts() != null
            && !firstCandidate.getContent().getParts().isEmpty()) {
          return firstCandidate.getContent().getParts().get(0).getText();
        }
      }
      return "No content found in response";
    } catch (Exception e) {
      return "Error Parsing: " + e.getMessage();
    }
  }

  private String buildPrompt(ResearchRequest request) {
    StringBuilder prompt = new StringBuilder();
    switch (request.getOperation()) {
      case "summarize":
        prompt.append(
            "Provide a clear and concise summary of the following "
                + "text in a few sentences:\n\n");
        break;
      case "suggest":
        prompt.append(
            "Based on the following content: suggest related topics "
                + "and further reading. Format the response with clear "
                + "headings and bullet points:\n\n");
        break;
      default:
        throw new IllegalArgumentException("Unknown Operation: " + request.getOperation());
    }
    prompt.append(request.getContent());
    return prompt.toString();
  }
}
