package it.unisannio.controlcomponentdecoder.service;

import it.unisannio.controlcomponentdecoder.model.SkillDetails;
import it.unisannio.controlcomponentdecoder.model.SkillInfo;
import it.unisannio.controlcomponentdecoder.model.SkillInputParameter;
import it.unisannio.controlcomponentdecoder.repository.MongoRepository;
import it.unisannio.controlcomponentdecoder.utils.Constants;
import it.unisannio.controlcomponentdecoder.utils.SkillMapper;
import it.unisannio.controlcomponentdecoder.utils.SubmodelExplorer;
import lombok.RequiredArgsConstructor;
import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultRelationshipElement;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ControlComponentService {

    private final MongoRepository mongoRepository;
    private final SkillMapper skillMapper;

    public List<SkillInfo> listSkillsByCapability(String capabilityIdShort) {
        return mongoRepository.getCapabilitiesRealizedBy(Constants.CAPABILITY_SEMANTIC_ID).stream()
                .filter(c -> {
                    String idShort = c.getFirst().getKeys().get((c.getFirst().getKeys().size()-1)).getValue();
                    return idShort.equals(capabilityIdShort);
                })
                .map(DefaultRelationshipElement::getSecond)
                .map(skillMapper::toSkillInfo)
                .toList();
    }

    public SkillDetails detailsSkill(String skillSubmodelId, String skillIdShort) {
        Submodel submodel = mongoRepository.findBySubmodelId(skillSubmodelId)
                .orElseThrow(() -> new RuntimeException("Invalid submodelId"));

        SubmodelExplorer explorer = new SubmodelExplorer(submodel);

        SubmodelElementCollection interfaces = explorer.getCollectionBySemantic(Constants.SEMANTIC_INTERFACE);
        SubmodelElementCollection targetInterface = explorer.getChildCollection(interfaces, skillIdShort);

        ReferenceElement ref = explorer.getReferenceBySemantic(targetInterface, Constants.SEMANTIC_INTERFACE_REF);
        String interfaceSubmodelId = ref.getValue().getKeys().get(0).getValue();
        String interfaceIdShort = ref.getValue().getKeys().get(1).getValue();
        String interfaceProperty = ref.getValue().getKeys().size() >= 4 ? ref.getValue().getKeys().get(4).getValue() : null;

        SubmodelElementCollection skills = explorer.getCollectionBySemantic(Constants.SEMANTIC_SKILLS);
        SubmodelElementCollection targetSkill = explorer.getChildCollection(skills, skillIdShort);

        SubmodelElementCollection parameters = targetSkill.getValue().stream()
                .filter(se -> se.getSemanticId().getKeys().get(0).getValue().equals(Constants.SEMANTIC_SKILL_PARAMETERS))
                .map(SubmodelElementCollection.class::cast)
                .findFirst()
                .orElseThrow();

        List<SkillInputParameter> inputParameters = parameters.getValue().stream()
                .map(SubmodelElementCollection.class::cast)
                .filter(param -> param.getValue().stream()
                        .filter(Property.class::isInstance)
                        .map(Property.class::cast)
                        .anyMatch(p -> p.getSemanticId().getKeys().get(0).getValue().equals(Constants.SEMANTIC_DIRECTION)
                                && p.getValue().equals("In")))
                .map(skillMapper::toSkillInputParameter)
                .toList();

        return new SkillDetails(skillIdShort, skillSubmodelId, interfaceSubmodelId, interfaceIdShort, interfaceProperty, inputParameters);
    }
}
