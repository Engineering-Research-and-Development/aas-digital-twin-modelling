package it.unisannio.controlcomponentdecoder.utils;

import it.unisannio.controlcomponentdecoder.model.SkillInfo;
import it.unisannio.controlcomponentdecoder.model.SkillInputParameter;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class SkillMapper {

    public SkillInfo toSkillInfo(Reference reference) {
        return new SkillInfo(
                reference.getKeys().get(0).getValue(),
                reference.getKeys().get(2).getValue()
        );
    }

    public SkillInputParameter toSkillInputParameter(SubmodelElementCollection input) {
        AtomicReference<String> typeStr = new AtomicReference<>();
        List<String> possibleValuesList = new ArrayList<>();

        input.getValue().forEach(v -> {
            String semantic = v.getSemanticId().getKeys().get(0).getValue();
            if (semantic.equals(Constants.SEMANTIC_PARAMETER_TYPE)) {
                typeStr.set(((Property) v).getValue());
            } else if (semantic.equals(Constants.SEMANTIC_PARAMETER_VALUES)) {
                ((SubmodelElementCollection) v).getValue().forEach(value -> possibleValuesList.add(((Property) value).getValue()));
            }
        });

        return new SkillInputParameter(input.getIdShort(), typeStr.get(), possibleValuesList);
    }
}
