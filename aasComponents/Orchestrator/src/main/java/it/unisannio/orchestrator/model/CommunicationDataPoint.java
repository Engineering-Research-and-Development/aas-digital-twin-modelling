package it.unisannio.orchestrator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunicationDataPoint {
    private String path;
    private String basPath;
    private String mediaType;
    private String protocol;
    private List<String> propertiesPath;
}
