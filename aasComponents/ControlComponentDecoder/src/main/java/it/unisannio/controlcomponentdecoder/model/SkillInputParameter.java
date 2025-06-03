package it.unisannio.controlcomponentdecoder.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillInputParameter {
    private String parameterName;
    private String type;
    private List<String>possibleValues;


}
