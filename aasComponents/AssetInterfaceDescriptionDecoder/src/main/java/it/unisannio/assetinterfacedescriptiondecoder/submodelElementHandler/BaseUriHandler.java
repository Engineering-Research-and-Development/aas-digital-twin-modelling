package it.unisannio.assetinterfacedescriptiondecoder.submodelElementHandler;

import it.unisannio.assetinterfacedescriptiondecoder.model.CommunicationInterfaceBuilder;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.springframework.stereotype.Service;

@Service
public class BaseUriHandler implements AIDSubmodelElementHandler {
    private static final String SEMANTIC_ID = "https://www.w3.org/2019/wot/td#baseURI";

    @Override
    public boolean canHandle(String semanticId) {
        return SEMANTIC_ID.equals(semanticId);
    }

    @Override
    public void handle(SubmodelElement element, CommunicationInterfaceBuilder builder) {
        builder.setBaseUriPath(((Property)element).getValue());
    }
}
