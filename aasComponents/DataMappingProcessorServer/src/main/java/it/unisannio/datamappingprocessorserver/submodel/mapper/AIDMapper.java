package it.unisannio.datamappingprocessorserver.submodel.mapper;

import it.unisannio.datamappingprocessorserver.model.CommunicationInterface;
import it.unisannio.datamappingprocessorserver.model.CommunicationInterfaceBuilder;
import it.unisannio.datamappingprocessorserver.submodelElementHandler.*;
import lombok.RequiredArgsConstructor;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AIDMapper {
    private final List<AIDSubmodelElementHandler> handlers;

    public List<CommunicationInterface> process(Submodel aSubmodel) {
        List<CommunicationInterface> communicationInterfaces = new ArrayList<>();

        for (SubmodelElement element : aSubmodel.getSubmodelElements()) {
            getSemanticId(element).ifPresent(semanticId -> {
                if ("https://admin-shell.io/idta/AssetInterfacesDescription/1/0/Interface".equals(semanticId)) {
                    SubmodelElementCollection interfaceElement = (SubmodelElementCollection) element;
                    CommunicationInterfaceBuilder builder = new CommunicationInterfaceBuilder();
                    builder.setSubmodelId(aSubmodel.getId());

                    String protocol = getProtocol(interfaceElement);
                    walkSubmodelElements(interfaceElement, builder);

                    CommunicationInterface ci = builder.build(protocol);
                    communicationInterfaces.add(ci);
                }
            });

        }

        return communicationInterfaces;
    }

    private void walkSubmodelElements(SubmodelElement element, CommunicationInterfaceBuilder builder) {
        getSemanticId(element).ifPresent(semanticId -> {
            if (element instanceof SubmodelElementCollection) {
                for (SubmodelElement child : ((SubmodelElementCollection) element).getValue()) {
                    walkSubmodelElements(child, builder);
                }
            }else if(element instanceof SubmodelElementList) {
                for (SubmodelElement child : ((SubmodelElementList) element).getValue()) {
                    walkSubmodelElements(child, builder);
                }
            }


            for (AIDSubmodelElementHandler handler : handlers) {
                if (handler.canHandle(semanticId)) {
                    handler.handle(element, builder);
                }
            }
        });
    }

    private Optional<String> getSemanticId(SubmodelElement element) {
        if(element.getSemanticId()!=null) {
            return Optional.of(element.getSemanticId().getKeys().get(0).getValue());
        }else{
            return Optional.empty();
        }
    }

    private String getProtocol(SubmodelElementCollection collection) {
        return collection.getSupplementalSemanticIds().get(0).getKeys().get(0).getValue();
    }
}
