package it.unisannio.controlcomponentdecoder.controller;

import it.unisannio.controlcomponentdecoder.model.SkillDetails;
import it.unisannio.controlcomponentdecoder.model.SkillInfo;
import it.unisannio.controlcomponentdecoder.service.ControlComponentService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@RestController()
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {
    private final ControlComponentService controlComponentService;


    @GetMapping
    public ResponseEntity<List<SkillInfo>> getControlComponent(@RequestParam(required = true) String capabilityIdShort){
        return ResponseEntity.ok(this.controlComponentService.listSkillsByCapability(capabilityIdShort));

    }

    @GetMapping("/{skillSubmodelId}/skillSubmodels/{skillIdShort}")
    public ResponseEntity<SkillDetails> getSkillDetails(@PathVariable String skillSubmodelId, @PathVariable String skillIdShort){
        return ResponseEntity.ok(
                this.controlComponentService.detailsSkill(
                        new String(Base64.getDecoder().decode(skillSubmodelId.getBytes())),
                        skillIdShort
                )
        );

    }
}
