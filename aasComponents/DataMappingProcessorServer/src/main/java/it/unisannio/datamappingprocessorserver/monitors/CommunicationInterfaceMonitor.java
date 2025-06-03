package it.unisannio.datamappingprocessorserver.monitors;


import it.unisannio.datamappingprocessorserver.model.MonitorException;
import it.unisannio.datamappingprocessorserver.model.CommunicationInterface;
import it.unisannio.datamappingprocessorserver.model.CommunicationInterfaceType;
import it.unisannio.datamappingprocessorserver.model.MappingConfiguration;

public interface CommunicationInterfaceMonitor {
    void start(MappingConfiguration mappingConfiguration, CommunicationInterface communicationInterface) throws MonitorException;
    CommunicationInterfaceType getType();
}
