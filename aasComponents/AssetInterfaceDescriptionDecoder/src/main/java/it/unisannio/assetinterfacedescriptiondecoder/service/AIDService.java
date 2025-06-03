package it.unisannio.assetinterfacedescriptiondecoder.service;

import it.unisannio.assetinterfacedescriptiondecoder.mapper.AIDMapper;
import it.unisannio.assetinterfacedescriptiondecoder.model.AIDProperty;
import it.unisannio.assetinterfacedescriptiondecoder.model.CommunicationDataPoint;
import it.unisannio.assetinterfacedescriptiondecoder.model.CommunicationInterface;
import it.unisannio.assetinterfacedescriptiondecoder.repository.MongoRepository;
import lombok.RequiredArgsConstructor;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

@Service
@RequiredArgsConstructor
public class AIDService {
    private final MongoRepository mongoRepository;
    private final AIDMapper aidMapper;


    public CommunicationDataPoint getCommunicationDataPoint(String submodelId, String interfaceIdShort, String propertyId) {
        Submodel interfacesDescriptions =  this.mongoRepository.findBySubmodelId(submodelId)
                .orElseThrow(()->new RuntimeException("Submodel with id: "+submodelId+" not found"));
        List<CommunicationInterface> interfaces = this.aidMapper.process(interfacesDescriptions);
        CommunicationInterface targetInterface = interfaces.stream()
                .filter(i->i.getPropertiesPath().containsKey(propertyId))
                .filter(i->{
                    StringTokenizer tokenizer = new StringTokenizer(i.getInterfaceId(),".");
                    String token=null;
                    while(tokenizer.hasMoreTokens()) {
                        token = tokenizer.nextToken();
                    }
                    return token.equals(interfaceIdShort);
                })
                .findFirst()
                .orElseThrow(()->new RuntimeException("Invalid property id or interface id"));

        AIDProperty targetProperty= targetInterface.getPropertiesPath().entrySet()
                .stream()
                .filter(entry->entry.getKey().equals(propertyId))
                .map(Map.Entry::getValue)
                .findFirst()
                .get();
        return new CommunicationDataPoint(
                targetProperty.getDataPointPath(),
                targetInterface.getBasePath(),
                targetInterface.getContentType(),targetInterface.getInterfaceType().name(),
                targetProperty.getPropertyPath()
        );
    }

}
