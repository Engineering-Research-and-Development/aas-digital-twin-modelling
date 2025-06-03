package it.unisannio.assetinterfacedescriptiondecoder.controller;

import it.unisannio.assetinterfacedescriptiondecoder.model.CommunicationDataPoint;
import it.unisannio.assetinterfacedescriptiondecoder.service.AIDService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/aidSubmodels")
@RequiredArgsConstructor
public class AssetInterfaceDescriptionController {
    private final AIDService aidService;

    @GetMapping("/{submodelId}/interfaceDescriptions/{interfaceIdShort}/dataPoints/{datapointIdShort}")
    public ResponseEntity<CommunicationDataPoint> getCommunicationDataPoint(
            @PathVariable String submodelId,
            @PathVariable String interfaceIdShort,
            @PathVariable String datapointIdShort
            ) {
        return ResponseEntity.ok(
                aidService.getCommunicationDataPoint(
                        new String(Base64.getDecoder().decode(submodelId.getBytes())),
                        interfaceIdShort,
                        datapointIdShort
                )
        );
    }
}
