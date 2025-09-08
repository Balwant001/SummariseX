package com.agents.research;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.agents.ReadonlyContext;
import com.google.adk.artifacts.BaseArtifactService;
import com.google.adk.artifacts.InMemoryArtifactService;
import com.google.adk.runner.Runner;
import com.google.adk.sessions.BaseSessionService;
import com.google.adk.sessions.InMemorySessionService;
import com.google.adk.sessions.State;
import com.google.adk.tools.BaseTool;
import com.google.adk.tools.ToolContext;
import com.google.common.collect.ImmutableList;
import io.a2a.A2A;
import io.a2a.spec.AgentCard;
import io.a2a.spec.EventKind;
import io.a2a.spec.Message.Role;
import io.a2a.spec.Part;
import io.a2a.spec.SendMessageResponse;
import io.a2a.spec.Task;
import io.a2a.spec.TextPart;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * The ResearchAgent class is responsible for initiating research workflows, and is a Host Agent
 * which can communicate to other Agents, hence extending it to perform various tasks.
 */
public class ResearchAgent {
  private static final Logger ADK_LOGGER = Logger.getLogger(ResearchAgent.class.getName());

  public BaseAgent ROOT_AGENT;
  public Runner RUNNER;
  private static final String AGENT_NAME = "Research_Agent";
  private static final String MODEL_NAME = "gemini-2.0-flash";

  private String agents = "";
  private Map<String, AgentCard> agentCards = new HashMap<>();
  private Map<String, RemoteAgentConnection> remoteAgentConnections = new HashMap<>();

  public ResearchAgent() {
    SendMessageTool sendMessageTool = new SendMessageTool(this);
    List<BaseTool> tools = ImmutableList.of(sendMessageTool);

    this.ROOT_AGENT =
        LlmAgent.builder()
            .name(AGENT_NAME)
            .model(MODEL_NAME)
            .instruction("")
            .description("This Host agent orchestrates research")
            .tools(tools)
            .build();

    BaseArtifactService artifactService = new InMemoryArtifactService();
    BaseSessionService sessionService = new InMemorySessionService();
    this.RUNNER = new Runner(ROOT_AGENT, AGENT_NAME, artifactService, sessionService);

    // Initialize Host Agent
    List<String> AgentURLs = List.of("http://localhost:10001", "http://localhost:10002");
    for (String url : AgentURLs) {
      try {
        AgentCard card = A2A.getAgentCard(url);
        RemoteAgentConnection connection = new RemoteAgentConnection(card);

        remoteAgentConnections.put(card.name(), connection);
        agentCards.put(card.name(), card);
        ADK_LOGGER.info("Fetched agentCard for: `" + url + "` with agent:" + card.name());

      } catch (Exception e) {
        ADK_LOGGER.warning("Unable to fetch AgentCard for: " + url);
        continue;
      }
    }
  }

  public List<JsonNode> sendMessage(String agentName, String task, ToolContext toolContext) {
    State state = toolContext.state();
    String taskId = (String) state.getOrDefault("task_id", UUID.randomUUID().toString());
    String contextId = (String) state.getOrDefault("context_id", UUID.randomUUID().toString());
    String messageId = UUID.randomUUID().toString();

    Role role = Role.USER;
    List<Part<?>> parts = List.of(new TextPart(task));

    RemoteAgentConnection connection = remoteAgentConnections.get(agentName);
    if (connection == null) {
      ADK_LOGGER.warning("No connection found for agent: " + agentName);
      return List.of();
    }

    try {
      SendMessageResponse res = connection.sendMessage(role, parts, messageId, taskId, contextId);
      if (res.getError() != null) {
        ADK_LOGGER.severe("RPC Error: " + res.getError().getMessage());
      }

      EventKind kind = res.getResult();
      if (kind == null || kind.getKind() != Task.TASK) {
        ADK_LOGGER.severe("Received a non-success or non-task response. Cannot proceed.");
        return List.of();
      }

      ObjectMapper objectMapper = new ObjectMapper();
      String jsonResponse = objectMapper.writeValueAsString(res);
      JsonNode jsonNode = objectMapper.readTree(jsonResponse);
      ADK_LOGGER.info("send_response: " + jsonResponse);

      JsonNode resultNode = jsonNode.path("result");
      List<JsonNode> resp = new ArrayList<>();

      JsonNode artifactsNode = resultNode.path("artifacts");
      if (artifactsNode.isArray()) {
        for (JsonNode artifact : artifactsNode) {
          JsonNode partsNode = artifact.path("parts");
          if (partsNode.isArray()) {
            for (JsonNode part : partsNode) {
              resp.add(part);
            }
          }
        }
      }
      return resp;

    } catch (Exception e) {
      ADK_LOGGER.severe("Error sending message to " + agentName + ": " + e.getMessage());
      return List.of();
    }
  }

  private String rootInstruction(ReadonlyContext context) {
    return "";
  }
}
