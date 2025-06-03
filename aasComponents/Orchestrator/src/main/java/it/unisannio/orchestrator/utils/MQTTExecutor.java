package it.unisannio.orchestrator.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unisannio.orchestrator.model.CapabilityExecutionCommand;
import it.unisannio.orchestrator.model.CommunicationDataPoint;
import it.unisannio.orchestrator.model.SkillDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class MQTTExecutor {

    private final ResultNotifier resultNotifier;
    private final Map<String, MqttClient> clients = new ConcurrentHashMap<>();
    private final Map<String, Consumer<String>> listeners = new ConcurrentHashMap<>();

    public void execute(CommunicationDataPoint dataPoint, SkillDetails skillDetails, CapabilityExecutionCommand command) {
        Consumer<String> messageHandler = payload -> {
            try {
                if (dataPoint.getMediaType().equals("text/plain")) {
                    String value = payload;
                    this.resultNotifier.sendResult(command.getResultListenerUrl(),Map.of("",value));
                } else if (dataPoint.getMediaType().equals("application/json")) {

                    JsonNode mapped = new ObjectMapper().readTree(payload);
                    Map<String,Object> result = this.buildDynamicObject(mapped,dataPoint.getPropertiesPath());
                    this.resultNotifier.sendResult(command.getResultListenerUrl(),result);
                }

            } catch (JsonProcessingException e) {
                log.error("error while decoding json payload: {}",e.getMessage());
            }
        };


        try {
            String clientId = skillDetails.getSkillSubmodelId() + "/" + skillDetails.getSkillIdShort();
            listeners.put(clientId, messageHandler);

            if (!clients.containsKey(clientId)) {
                MqttClient client = new MqttClient(dataPoint.getBasPath(), clientId);
                clients.put(clientId, client);
                client.connect();

                client.subscribe(dataPoint.getPath(), (receivedTopic, mqttMessage) -> {
                    String payload = new String(mqttMessage.getPayload());
                    Consumer<String> handler = listeners.get(clientId);
                    if (handler != null) {
                        handler.accept(payload);
                    } else {
                        log.warn("No handler for interface {}", clientId);
                    }
                });

                log.info("✅ MQTT client started and subscribed: {}", clientId);
            } else {
                log.info("🔁 MQTT client already exists, handler updated: {}", clientId);
            }
        }catch (MqttException e) {
            log.error("Error while establishing mqtt connection: {}",e.getMessage());
        }



    }


    public Map<String, Object> buildDynamicObject(JsonNode mapped, List<String> propertiesPath) {
        Map<String, Object> result = new HashMap<>();

        for (String path : propertiesPath) {
            String[] parts = path.split("\\.");
            Map<String, Object> current = result;

            for (int i = 0; i < parts.length; i++) {
                String key = parts[i];

                if (i == parts.length - 1) {
                    // Ultimo nodo, assegna valore
                    String value = mapped.at("/" + String.join("/", parts)).asText();
                    current.put(key, value);
                } else {
                    // Nodo intermedio, costruisci sotto-mappa se non esiste
                    if (!current.containsKey(key) || !(current.get(key) instanceof Map)) {
                        current.put(key, new HashMap<String, Object>());
                    }
                    current = (Map<String, Object>) current.get(key);
                }
            }
        }

        return result;
    }
}
