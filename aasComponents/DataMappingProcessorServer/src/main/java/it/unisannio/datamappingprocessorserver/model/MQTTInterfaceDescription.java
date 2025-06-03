package it.unisannio.datamappingprocessorserver.model;

import lombok.*;

import java.util.Map;


@AllArgsConstructor
@Getter
public class MQTTInterfaceDescription implements CommunicationInterface {
    private Map<String,String> topic;
    private String brokerBaseUri;
    private String interfaceId;
    private String mediaType;

    @Override
    public CommunicationInterfaceType getInterfaceType() {
        return CommunicationInterfaceType.MQTT;
    }
}
