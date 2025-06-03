package it.unisannio.orchestrator.repository;

import it.unisannio.orchestrator.model.CommunicationDataPoint;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url = "http://localhost:8093", name = "aid",path = "aidSubmodels")
public interface AssetInterfaceDescriptionDecoderRepository {
    @GetMapping("/{submodelId}/interfaceDescriptions/{interfaceIdShort}/dataPoints/{datapointIdShort}")
    CommunicationDataPoint getCommunicationDataPoint(
            @PathVariable String submodelId,
            @PathVariable String interfaceIdShort,
            @PathVariable String datapointIdShort
    );
}
