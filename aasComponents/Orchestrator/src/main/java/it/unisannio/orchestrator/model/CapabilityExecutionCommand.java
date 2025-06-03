package it.unisannio.orchestrator.model;

import lombok.Data;

@Data
public class CapabilityExecutionCommand {
    private String capabilityIdShort;
    private String resultListenerUrl;
}
