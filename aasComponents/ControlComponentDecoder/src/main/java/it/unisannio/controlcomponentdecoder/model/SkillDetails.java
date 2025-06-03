package it.unisannio.controlcomponentdecoder.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDetails{
    private String skillSubmodelId;
    private String skillIdShort;
    private String interfaceSubmodelId;
    private String interfaceIdShort;
    private String interfacePropertyName;
    private List<SkillInputParameter> inputs;
}
