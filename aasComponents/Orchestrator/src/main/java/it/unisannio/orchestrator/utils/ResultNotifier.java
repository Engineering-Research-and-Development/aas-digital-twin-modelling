package it.unisannio.orchestrator.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResultNotifier {
    private final WebClient.Builder webClientBuilder;

    public void sendResult(String url, Map<String, Object> result) {
        result.put("correlationId","pc_energy_consumption");
        webClientBuilder.build()
                .post()
                .uri(url)
                .bodyValue(result)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(e -> {
                    log.error("Error during notification {} ",e.getMessage());
                    return Mono.error(e);
                })
                .subscribe();
    }
}
