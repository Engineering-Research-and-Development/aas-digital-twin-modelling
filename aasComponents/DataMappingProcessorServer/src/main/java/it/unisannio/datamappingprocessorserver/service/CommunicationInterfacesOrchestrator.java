package it.unisannio.datamappingprocessorserver.service;

import it.unisannio.datamappingprocessorserver.model.*;
import it.unisannio.datamappingprocessorserver.monitors.CommunicationInterfaceMonitor;
import it.unisannio.datamappingprocessorserver.monitors.MQTTMonitor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CommunicationInterfacesOrchestrator {
    private final List<CommunicationInterfaceMonitor> monitors;
    private final EnumMap<CommunicationInterfaceType,CommunicationInterfaceMonitor> monitorsByType= new EnumMap<>(CommunicationInterfaceType.class);


    @PostConstruct
    private void initMap(){
        this.monitors.forEach(monitor-> monitorsByType.put(monitor.getType(),monitor));
    }

    public void monitor(CommunicationInterface communicationInterface, MappingConfiguration mappingConfiguration) {
        monitorsByType.get(communicationInterface.getInterfaceType()).start(mappingConfiguration,communicationInterface);
    }
}
