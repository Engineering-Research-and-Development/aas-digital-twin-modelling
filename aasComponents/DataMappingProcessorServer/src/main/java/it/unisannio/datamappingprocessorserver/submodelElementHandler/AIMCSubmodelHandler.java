package it.unisannio.datamappingprocessorserver.submodelElementHandler;

import it.unisannio.datamappingprocessorserver.model.MappingConfigurationBuilder;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;

public interface AIMCSubmodelHandler {
    boolean canHandle(String semanticId);
    void handle(SubmodelElement element, MappingConfigurationBuilder builder);
}
