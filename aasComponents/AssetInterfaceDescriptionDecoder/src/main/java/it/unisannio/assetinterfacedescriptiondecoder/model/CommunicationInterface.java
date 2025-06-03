package it.unisannio.assetinterfacedescriptiondecoder.model;

import java.util.Map;

public interface CommunicationInterface {
    String getInterfaceId();
    CommunicationInterfaceType getInterfaceType();
    String getBasePath();
    String getContentType();
    Map<String,AIDProperty> getPropertiesPath();
}
