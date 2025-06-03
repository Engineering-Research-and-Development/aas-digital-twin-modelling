package it.unisannio.datamappingprocessorserver.submodelElementHandler;

import it.unisannio.datamappingprocessorserver.model.MappingConfigurationBuilder;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.springframework.stereotype.Service;

@Service
public class InterfaceReferenceIdHandler implements AIMCSubmodelHandler {
    private final String SEMANTIC_ID = "https://admin-shell.io/idta/AssetInterfacesMappingConfiguration/1/0/InterfaceReference";
    @Override
    public boolean canHandle(String semanticId) {
        return SEMANTIC_ID.equals(semanticId);
    }

    @Override
    public void handle(SubmodelElement element, MappingConfigurationBuilder builder) {
        ReferenceElement reference = (ReferenceElement) element;
        StringBuilder stringBuilder = new StringBuilder();
        reference.getValue().getKeys().forEach(key -> stringBuilder.append(key.getValue()).append("."));
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        builder.setInterfaceReference(stringBuilder.toString());
    }
}
