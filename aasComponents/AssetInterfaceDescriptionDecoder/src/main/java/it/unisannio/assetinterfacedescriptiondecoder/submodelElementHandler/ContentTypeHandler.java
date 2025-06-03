package it.unisannio.assetinterfacedescriptiondecoder.submodelElementHandler;

import it.unisannio.assetinterfacedescriptiondecoder.model.CommunicationInterfaceBuilder;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.springframework.stereotype.Service;

@Service
public class ContentTypeHandler implements AIDSubmodelElementHandler {
    private final String SEMANTIC_ID="https://www.w3.org/2019/wot/hypermedia#forContentType";
    @Override
    public boolean canHandle(String semanticId) {
        return this.SEMANTIC_ID.equals(semanticId);
    }

    @Override
    public void handle(SubmodelElement element, CommunicationInterfaceBuilder builder) {
        Property property = (Property) element;
        builder.setMediaType(property.getValue());
    }
}
