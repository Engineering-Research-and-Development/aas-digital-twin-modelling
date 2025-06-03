package it.unisannio.assetinterfacedescriptiondecoder.model;

import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunicationInterfaceBuilder {
    @Setter
    private String baseUriPath;

    @Setter
    private String submodelId;

    private Map<String,AIDProperty> topicByProperty = new HashMap<>();

    @Setter
    private String mediaType;





    public CommunicationInterface build(String protocol) {
        switch (protocol) {
            case "http://www.w3.org/2011/mqtt":
                return new MQTTInterfaceDescription(topicByProperty, baseUriPath, submodelId,mediaType);
            default:
                throw new UnsupportedOperationException("Unsupported protocol: " + protocol);
        }
    }

    public void appendShortId(String idShort) {
        this.submodelId = this.submodelId!=null?
                this.submodelId+"."+idShort:
                idShort;
    }


    public void addTopicByProperty(Map<String,AIDProperty> paths) {
        this.topicByProperty.putAll(paths);
    }
}
