package com.agents.research;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.adk.sessions.State;
import com.google.adk.tools.BaseTool;
import com.google.adk.tools.ToolContext;
import com.google.genai.types.FunctionDeclaration;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import io.reactivex.rxjava3.core.Single;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SendMessageTool extends BaseTool {
  private final ResearchAgent agentInstance;
  private static final String NAME_PARAMETER = "agent_name";
  private static final String TASK_PARAMETER = "task";

  public SendMessageTool(ResearchAgent agentInstance) {
    super("send_message", "Sends message to all the other tools");
    this.agentInstance = agentInstance;
  }

  @Override
  public Optional<FunctionDeclaration> declaration() {
    Map<String, Schema> properties = new HashMap<>();

    properties.put(
        NAME_PARAMETER,
        Schema.builder()
            .type(Type.Known.STRING)
            .description("The name of the agent to send the message to")
            .build());
    properties.put(
        TASK_PARAMETER,
        Schema.builder()
            .type(Type.Known.STRING)
            .description("The task or message to send to the agent")
            .build());

    Schema parameterSchema =
        Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(properties)
            .required(List.of("agent_name", "task"))
            .build();

    FunctionDeclaration declaration =
        FunctionDeclaration.builder()
            .name(name())
            .description(description())
            .parameters(parameterSchema)
            .build();
    return Optional.of(declaration);
  }

  @Override
  public Single<Map<String, Object>> runAsync(Map<String, Object> args, ToolContext toolContext) {
    try {
      State state = toolContext.state();
      String agentName = (String) state.get("agent_name");
      String task = (String) state.get("task");
      List<JsonNode> result = agentInstance.sendMessage(agentName, task, toolContext);

      Map<String, Object> response = new HashMap<>();
      response.put("result", result);
      return Single.just(response);
    } catch (Exception e) {
      Map<String, Object> errorResp = new HashMap<>();
      errorResp.put("error", "Failed to send message: " + e.getMessage());
      return Single.just(errorResp);
    }
  }
}
