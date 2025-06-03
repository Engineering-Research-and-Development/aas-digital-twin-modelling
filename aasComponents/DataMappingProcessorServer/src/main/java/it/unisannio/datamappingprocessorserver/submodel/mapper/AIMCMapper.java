package it.unisannio.datamappingprocessorserver.submodel.mapper;

import it.unisannio.datamappingprocessorserver.model.MappingConfigurationBuilder;
import it.unisannio.datamappingprocessorserver.model.MappingConfiguration;
import it.unisannio.datamappingprocessorserver.submodelElementHandler.AIMCSubmodelHandler;
import lombok.RequiredArgsConstructor;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AIMCMapper {
    private final List<AIMCSubmodelHandler> handlers;

    public List<MappingConfiguration> process(Submodel aSubmodel) {
        List<MappingConfiguration> mappingConfigurations = new ArrayList<>();

        for (SubmodelElement element : aSubmodel.getSubmodelElements()) {
            getSemanticId(element).ifPresent(semanticId -> {
                if ("https://admin-shell.io/idta/AssetInterfacesMappingConfiguration/1/0/MappingConfigurations".equals(semanticId)) {
                    SubmodelElementList mapConfs = (SubmodelElementList) element;
                    for(SubmodelElement mapConf : mapConfs.getValue()){
                        MappingConfigurationBuilder builder = new MappingConfigurationBuilder();

                        walkSubmodelElements(mapConf, builder);

                        MappingConfiguration mc = builder.build();
                        mappingConfigurations.add(mc);
                    }

                }
            });

        }

        return mappingConfigurations;
    }

    private void walkSubmodelElements(SubmodelElement element, MappingConfigurationBuilder builder) {
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


            for (AIMCSubmodelHandler handler : handlers) {
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

}
