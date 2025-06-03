package it.unisannio.orchestrator.service;

import it.unisannio.orchestrator.model.CapabilityExecutionCommand;
import it.unisannio.orchestrator.model.CommunicationDataPoint;
import it.unisannio.orchestrator.model.SkillDetails;
import it.unisannio.orchestrator.model.SkillInfo;
import it.unisannio.orchestrator.repository.AssetInterfaceDescriptionDecoderRepository;
import it.unisannio.orchestrator.repository.ControlComponentDecoderRepository;
import it.unisannio.orchestrator.utils.MQTTExecutor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CapabilityExecutorService {
    private final AssetInterfaceDescriptionDecoderRepository aidRepository;
    private final ControlComponentDecoderRepository ccdRepository;
    private final MQTTExecutor mqttExecutor;
//    @PostConstruct
//    public void init() {
//        CapabilityExecutionCommand cmd= new CapabilityExecutionCommand();
//        cmd.setResultListenerUrl("measuringEnergyConsumption4");
//        cmd.setCapabilityIdShort("MeasuringEnergyConsumption");
//        this.execute(cmd);
//    }

    public void execute(CapabilityExecutionCommand command) {
        List<SkillInfo> skills = this.ccdRepository.getControlComponent(command.getCapabilityIdShort());
        //TODO andrebbe selezionata la migliore, per ora seleziono la prima
        SkillInfo targetSkill = skills.stream().filter(s ->s.getSkillSubmodelId().equals("https://example.com/ids/sm/4301_7120_5052_6357")).findFirst().get();
        SkillDetails skillDetails = this.ccdRepository.getSkillDetails(
                Base64.getEncoder().encodeToString(targetSkill.getSkillSubmodelId().getBytes()),
                targetSkill.getSkillIdShort()
        );
        CommunicationDataPoint communicationDataPoint = this.aidRepository.getCommunicationDataPoint(
                Base64.getEncoder().encodeToString(skillDetails.getInterfaceSubmodelId().getBytes()),
                skillDetails.getInterfaceIdShort(),
                skillDetails.getInterfacePropertyName()
        );
        if(communicationDataPoint.getProtocol().equals("MQTT")){
            this.mqttExecutor.execute(communicationDataPoint,skillDetails,command);
        }
    }
}
