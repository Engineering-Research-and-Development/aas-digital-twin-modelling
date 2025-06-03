package it.unisannio.datamappingprocessorserver.submodelElementHandler;

import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.springframework.stereotype.Service;

@Service
public class HrefHandler{
    private static final String SEMANTIC_ID = "https://www.w3.org/2019/wot/hypermedia#hasTarget";

    public boolean canHandle(String semanticId) {
        return SEMANTIC_ID.equals(semanticId);
    }

    public String handle(SubmodelElement element) {
       return ((Property)element).getValue();
    }
}
