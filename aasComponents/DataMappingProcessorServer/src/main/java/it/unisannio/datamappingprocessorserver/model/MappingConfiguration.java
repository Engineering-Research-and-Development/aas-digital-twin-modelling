package it.unisannio.datamappingprocessorserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class MappingConfiguration {
    private String interfaceReference;

    Map<String,SubSpec> sources;
    Map<String,SubSpec> targets;
//    private List<SubSpec> sources;
//    private List<SubSpec> targets;


    @AllArgsConstructor
    @Data
    public static class SubSpec{
        private String submodelId;
        private String submodelShortId;
    }
}


