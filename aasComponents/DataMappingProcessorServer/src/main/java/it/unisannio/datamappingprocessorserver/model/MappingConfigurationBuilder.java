package it.unisannio.datamappingprocessorserver.model;

import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
public class MappingConfigurationBuilder {
    private String interfaceReference;
    private Map<String,MappingConfiguration.SubSpec> sources = new HashMap<>();
    private Map<String,MappingConfiguration.SubSpec> targets= new HashMap<>();





    public MappingConfiguration build() {
        return new MappingConfiguration(interfaceReference,sources,targets);
    }

    public void addSource(String property, MappingConfiguration.SubSpec aSource) {
        this.sources.put(property,aSource);
    }

    public void addSink(String property, MappingConfiguration.SubSpec aSink) {
        this.targets.put(property,aSink);
    }
}
