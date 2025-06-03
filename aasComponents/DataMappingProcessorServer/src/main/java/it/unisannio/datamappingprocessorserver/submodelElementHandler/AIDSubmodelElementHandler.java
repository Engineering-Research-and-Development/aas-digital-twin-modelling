package it.unisannio.datamappingprocessorserver.submodelElementHandler;

import it.unisannio.datamappingprocessorserver.model.CommunicationInterfaceBuilder;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;

public interface AIDSubmodelElementHandler {
    boolean canHandle(String semanticId);
    void handle(SubmodelElement element, CommunicationInterfaceBuilder builder);
}
