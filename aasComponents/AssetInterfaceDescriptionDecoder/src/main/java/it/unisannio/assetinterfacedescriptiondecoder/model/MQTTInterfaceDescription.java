package it.unisannio.assetinterfacedescriptiondecoder.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;


@AllArgsConstructor
@Getter
public class MQTTInterfaceDescription implements CommunicationInterface {
    private Map<String,AIDProperty> topic;
    private String brokerBaseUri;
    private String interfaceId;
    private String contentType;

    @Override
    public CommunicationInterfaceType getInterfaceType() {
        return CommunicationInterfaceType.MQTT;
    }

    @Override
    public String getBasePath() {
        return brokerBaseUri;
    }

    @Override
    public Map<String,AIDProperty> getPropertiesPath() {
        return topic;
    }

}
