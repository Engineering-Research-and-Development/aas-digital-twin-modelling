package it.unisannio.datamappingprocessorserver.monitors;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unisannio.datamappingprocessorserver.model.MonitorException;
import it.unisannio.datamappingprocessorserver.model.CommunicationInterface;
import it.unisannio.datamappingprocessorserver.model.CommunicationInterfaceType;
import it.unisannio.datamappingprocessorserver.model.MQTTInterfaceDescription;
import it.unisannio.datamappingprocessorserver.model.MappingConfiguration;
import it.unisannio.datamappingprocessorserver.repository.MongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class MQTTMonitor implements CommunicationInterfaceMonitor {
    private final Map<String, MqttClient> clients = new ConcurrentHashMap<>();
    private final Map<String, Consumer<String>> listeners = new ConcurrentHashMap<>();
    private final MongoRepository mongoRepository;

    @Override
    public void start(MappingConfiguration mappingConfiguration, CommunicationInterface communicationInterface) throws MonitorException {
        MQTTInterfaceDescription description = (MQTTInterfaceDescription) communicationInterface;


        String interfaceId = communicationInterface.getInterfaceId();
        description.getTopic().forEach((aProp, aTopic) -> {
            try {
                Consumer<String> messageHandler = payload -> {
                    try {
                        String patchValue = null;
                        if (description.getMediaType().equals("text/plain")) {
                            patchValue = payload;
                        } else if (description.getMediaType().equals("application/json")) {

                            JsonNode mapped = new ObjectMapper().readTree(payload);
                            patchValue = mapped.path(aProp).asText();
                        }
                        MappingConfiguration.SubSpec target = mappingConfiguration.getTargets().get(aProp);
                        this.mongoRepository.updateValueByIdShort(target.getSubmodelId(),target.getSubmodelShortId(),patchValue);

                    } catch (Exception e) {
                        log.error("Error processing message: {}", e.getMessage(), e);
                    }
                };
                String clientId = interfaceId+aTopic+aProp;
                listeners.put(clientId, messageHandler);

                if (!clients.containsKey(clientId)) {
                    MqttClient client = new MqttClient(description.getBrokerBaseUri(), clientId);
                    clients.put(clientId, client);
                    client.connect();

                    client.subscribe(aTopic, (receivedTopic, mqttMessage) -> {
                        String payload = new String(mqttMessage.getPayload());
                        Consumer<String> handler = listeners.get(clientId);
                        if (handler != null) {
                            handler.accept(payload);
                        } else {
                            log.warn("No handler for interface {}", interfaceId);
                        }
                    });

                    log.info("✅ MQTT client started and subscribed: {}", clientId);
                } else {
                    log.info("🔁 MQTT client already exists, handler updated: {}", clientId);
                }
            } catch (MqttException e) {
                throw new MonitorException("MQTT error: " + e.getMessage());

            }

        });

    }

    @Override
    public CommunicationInterfaceType getType() {
        return CommunicationInterfaceType.MQTT;
    }


}
