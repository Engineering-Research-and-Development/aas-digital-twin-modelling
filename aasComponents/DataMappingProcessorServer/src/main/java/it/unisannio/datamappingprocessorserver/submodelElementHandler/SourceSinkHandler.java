package it.unisannio.datamappingprocessorserver.submodelElementHandler;

import it.unisannio.datamappingprocessorserver.model.MappingConfiguration;
import it.unisannio.datamappingprocessorserver.model.MappingConfigurationBuilder;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.RelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.springframework.stereotype.Service;

@Service
public class SourceSinkHandler implements AIMCSubmodelHandler {
    private final String SEMANTIC_ID="https://admin-shell.io/idta/AssetInterfacesMappingConfiguration/1/0/MappingSourceSinkRelation";
    @Override
    public boolean canHandle(String semanticId) {
        return SEMANTIC_ID.equals(semanticId);
    }

    @Override
    public void handle(SubmodelElement element, MappingConfigurationBuilder builder) {
        RelationshipElement sourceSink = (RelationshipElement) element;
        Reference source = sourceSink.getFirst();
        Reference sink = sourceSink.getSecond();


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(source.getKeys().get(1).getValue());
        for(int i=2; i<source.getKeys().size(); i++) {
            stringBuilder.append(".").append(source.getKeys().get(i).getValue());
        }
        String propertyName = source.getKeys().get(source.getKeys().size() - 1).getValue();
        MappingConfiguration.SubSpec aSource = new MappingConfiguration.SubSpec(source.getKeys().get(0).getValue(),stringBuilder.toString());


        stringBuilder = new StringBuilder();
        stringBuilder.append(sink.getKeys().get(1).getValue());
        for(int i=2; i<sink.getKeys().size(); i++) {
            stringBuilder.append(".").append(sink.getKeys().get(i).getValue());
        }

        MappingConfiguration.SubSpec aSink = new MappingConfiguration.SubSpec(sink.getKeys().get(0).getValue(),stringBuilder.toString());
        builder.addSource(propertyName,aSource);
        builder.addSink(propertyName,aSink);
    }
}
