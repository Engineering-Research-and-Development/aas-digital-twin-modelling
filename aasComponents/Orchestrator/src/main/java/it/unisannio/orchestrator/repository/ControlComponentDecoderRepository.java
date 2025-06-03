package it.unisannio.orchestrator.repository;

import it.unisannio.orchestrator.model.SkillDetails;
import it.unisannio.orchestrator.model.SkillInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(url = "http://localhost:8092",name = "ccd", path = "skills")
public interface ControlComponentDecoderRepository {
    @GetMapping
    List<SkillInfo> getControlComponent(@RequestParam(required = true) String capabilityIdShort);

    @GetMapping("/{skillSubmodelId}/skillSubmodels/{skillIdShort}")
    SkillDetails getSkillDetails(@PathVariable String skillSubmodelId, @PathVariable String skillIdShort);
}
