package it.unisannio.datamappingprocessorserver.service;

import it.unisannio.datamappingprocessorserver.repository.MongoRepository;
import it.unisannio.datamappingprocessorserver.model.CommunicationInterface;
import it.unisannio.datamappingprocessorserver.model.MappingConfiguration;
import it.unisannio.datamappingprocessorserver.submodel.mapper.AIDMapper;
import it.unisannio.datamappingprocessorserver.submodel.mapper.AIMCMapper;
import lombok.RequiredArgsConstructor;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SubmodelDiscoveryService {
    private final AIDMapper aidMapper;
    private final AIMCMapper aimcMapper;
    private final CommunicationInterfacesOrchestrator communicationInterfacesOrchestrator;
    private final Set<String> discoveredInterfaceDescription = new HashSet<>();
    @Value("${aid.semantic-id}")
    private String aidSemanticId;
    @Value("${aimc.semantic-id}")
    private String aimcSemanticId;
    private final MongoRepository mongoRepository;


    @Scheduled(fixedDelay = 60, timeUnit = TimeUnit.SECONDS)
    public void findAID(){
        Map<String, MappingConfiguration> mappingMap = this.discoverMappingConfigurations();

        List<Submodel> interfacesDescriptions =  this.mongoRepository.findAllBySemanticId(aidSemanticId);
        for (Submodel anInterface : interfacesDescriptions) {
            List<CommunicationInterface> communicationInterfaces = this.aidMapper.process(anInterface);
            for (CommunicationInterface communicationInterface : communicationInterfaces) {
                if (
                        !discoveredInterfaceDescription.contains(communicationInterface.getInterfaceId()) &&
                        mappingMap.containsKey(communicationInterface.getInterfaceId())
                ) {
                        communicationInterfacesOrchestrator.monitor(
                                communicationInterface,
                                mappingMap.get(communicationInterface.getInterfaceId())
                        );
                    }


            }
        }
    }

    private Map<String, MappingConfiguration>  discoverMappingConfigurations() {
        List<Submodel> mappings = this.mongoRepository.findAllBySemanticId(aimcSemanticId);
        Map<String, MappingConfiguration> mappingMap = new HashMap<>();

        for (Submodel aMapping : mappings) {
            List<MappingConfiguration> configs = aimcMapper.process(aMapping);
            for (MappingConfiguration conf : configs) {
                mappingMap.putIfAbsent(conf.getInterfaceReference(), conf);
            }
        }
        return mappingMap;
    }
}
