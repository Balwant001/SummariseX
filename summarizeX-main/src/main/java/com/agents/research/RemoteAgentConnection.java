package com.agents.research;

import io.a2a.client.A2AClient;
import io.a2a.spec.A2AServerException;
import io.a2a.spec.AgentCard;
import io.a2a.spec.Message;
import io.a2a.spec.Message.Role;
import io.a2a.spec.MessageSendParams;
import io.a2a.spec.Part;
import io.a2a.spec.SendMessageResponse;
import java.util.List;
import lombok.Getter;

/** A class to hold the connections to the Remote Agents. */
@Getter
public class RemoteAgentConnection {
  private A2AClient agentClient;
  private AgentCard agentCard;

  public RemoteAgentConnection(AgentCard agentCard) {
    this.agentClient = new A2AClient(agentCard);
    this.agentCard = agentCard;
  }

  public SendMessageResponse sendMessage(
      Role role, List<Part<?>> parts, String messageId, String taskId, String contextId)
      throws A2AServerException {
    Message message =
        new Message.Builder()
            .role(role)
            .parts(parts)
            .messageId(messageId)
            .taskId(taskId)
            .contextId(contextId)
            .build();

    MessageSendParams params = new MessageSendParams.Builder().message(message).build();
    return this.agentClient.sendMessage(params);
  }
}
