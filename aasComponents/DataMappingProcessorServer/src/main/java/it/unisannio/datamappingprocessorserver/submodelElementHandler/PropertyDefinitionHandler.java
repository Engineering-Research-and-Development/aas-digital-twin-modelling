package it.unisannio.datamappingprocessorserver.submodelElementHandler;

import it.unisannio.datamappingprocessorserver.model.CommunicationInterfaceBuilder;
import lombok.RequiredArgsConstructor;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyDefinitionHandler implements AIDSubmodelElementHandler {
    private final String SEMANTIC_ID="https://admin-shell.io/idta/AssetInterfacesDescription/1/0/PropertyDefinition";

    private final String KEY_SEMANTIC = "https://admin-shell.io/idta/AssetInterfacesDescription/1/0/key";
    private final String TYPE_SEMANTIC = "https://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    private final String PROPERTIES_SEMANTIC ="https://www.w3.org/2019/wot/json-schema#properties";
    private final String HAS_FORMS_SEMANTIC = "https://www.w3.org/2019/wot/td#hasForm";
    private final HrefHandler handler;

    @Override
    public boolean canHandle(String semanticId) {
        return this.SEMANTIC_ID.equals(semanticId);
    }

    @Override
    public void handle(SubmodelElement element, CommunicationInterfaceBuilder builder) {
        SubmodelElementCollection aProperty = (SubmodelElementCollection) element;
        Map<String,String> paths = extractPaths(aProperty);
        builder.addTopicByProperty(paths);
    }

    private Map<String,String> extractPaths(SubmodelElementCollection propertyCollection) {
        List<String> result = new ArrayList<>();
        String href = "";
        if(!propertyCollection.getValue().stream().anyMatch(v->v.getSemanticId().getKeys().get(0).getValue().equals("https://www.w3.org/2019/wot/td#hasForm"))){
            return new HashMap<>();
        }


        List<SubmodelElement> values = propertyCollection.getValue();
        String key = null;
        String type = null;

        for (SubmodelElement sub : values) {
            String semanticKey = sub.getSemanticId().getKeys().get(0).getValue();
            if (sub instanceof Property prop) {

                    if (KEY_SEMANTIC.equals(semanticKey)) {
                        key = prop.getValue();
                    } else if (TYPE_SEMANTIC.equals(semanticKey)) {
                        type = prop.getValue();
                    }
            }else if(semanticKey.equals(HAS_FORMS_SEMANTIC)){
                SubmodelElementCollection forms = (SubmodelElementCollection)sub;
                href = this.handler.handle(forms.getValue().get(0));
            }

        }

        if (key == null) {

            key = propertyCollection.getIdShort();
        }

        if ("object".equalsIgnoreCase(type)) {
            for (SubmodelElement child : values) {
                if (child instanceof SubmodelElementCollection col && PROPERTIES_SEMANTIC.equals(child.getSemanticId().getKeys().get(0).getValue())) {
                        for(SubmodelElement childProperties:col.getValue()){
                            if (childProperties instanceof SubmodelElementCollection childPropCollection) {
                                result.addAll(extractPaths(childPropCollection, ""));
                            }
                        }
                    }

            }
        } else {
            result.add(key);
        }

        String finalHref = href;
        return result.stream().map(p->Map.entry(p, finalHref))
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
    }

    private List<String> extractPaths(SubmodelElementCollection propertyCollection, String prefix) {
        List<String> result = new ArrayList<>();



        List<SubmodelElement> values = propertyCollection.getValue();
        String key = null;
        String type = null;

        for (SubmodelElement sub : values) {
            if (sub instanceof Property prop) {
                String semanticKey = prop.getSemanticId().getKeys().get(0).getValue();
                if (KEY_SEMANTIC.equals(semanticKey)) {
                    key = prop.getValue();
                } else if (TYPE_SEMANTIC.equals(semanticKey)) {
                    type = prop.getValue();
                }
            }

        }

        if (key == null) {

            key = propertyCollection.getIdShort();
        }

        String fullPath = prefix.isEmpty() ? key : prefix + "." + key;

        if ("object".equalsIgnoreCase(type)) {
            for (SubmodelElement child : values) {
                if (child instanceof SubmodelElementCollection col && PROPERTIES_SEMANTIC.equals(child.getSemanticId().getKeys().get(0).getValue())) {
                        for(SubmodelElement childProperties:col.getValue()){
                            if (childProperties instanceof SubmodelElementCollection childPropCollection) {
                                result.addAll(extractPaths(childPropCollection, fullPath));
                            }
                        }
                    }

            }
        } else {
            result.add(fullPath);
        }


        return result;
    }
}
