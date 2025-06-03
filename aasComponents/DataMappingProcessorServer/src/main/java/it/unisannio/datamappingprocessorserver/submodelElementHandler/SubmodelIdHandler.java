package it.unisannio.datamappingprocessorserver.submodelElementHandler;

import it.unisannio.datamappingprocessorserver.model.CommunicationInterfaceBuilder;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.springframework.stereotype.Service;

@Service
public class SubmodelIdHandler implements AIDSubmodelElementHandler {
    private static final String SEMANTIC_ID = "https://admin-shell.io/idta/AssetInterfacesDescription/1/0/Interface";

    @Override
    public boolean canHandle(String semanticId) {
        return SEMANTIC_ID.equals(semanticId);
    }

    @Override
    public void handle(SubmodelElement element, CommunicationInterfaceBuilder builder) {
        builder.appendShortId(element.getIdShort());
    }
}
