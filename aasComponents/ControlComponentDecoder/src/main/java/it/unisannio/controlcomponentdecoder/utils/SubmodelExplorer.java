package it.unisannio.controlcomponentdecoder.utils;

import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceElement;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;

public class SubmodelExplorer {
    private final Submodel submodel;

    public SubmodelExplorer(Submodel submodel) {
        this.submodel = submodel;
    }

    public SubmodelElementCollection getCollectionBySemantic(String semanticId) {
        return submodel.getSubmodelElements().stream()
                .filter(se -> se.getSemanticId().getKeys().get(0).getValue().equals(semanticId))
                .map(SubmodelElementCollection.class::cast)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Collection not found for semantic ID: " + semanticId));
    }

    public SubmodelElementCollection getChildCollection(SubmodelElementCollection parent, String idShort) {
        return parent.getValue().stream()
                .filter(e -> e.getIdShort().equals(idShort))
                .map(SubmodelElementCollection.class::cast)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Child not found: " + idShort));
    }

    public ReferenceElement getReferenceBySemantic(SubmodelElementCollection collection, String semantic) {
        return collection.getValue().stream()
                .filter(e -> e.getSemanticId().getKeys().get(0).getValue().equals(semantic))
                .map(ReferenceElement.class::cast)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Reference not found: " + semantic));
    }
}
