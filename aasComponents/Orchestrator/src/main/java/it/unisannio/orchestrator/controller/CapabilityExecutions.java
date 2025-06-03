package it.unisannio.orchestrator.controller;

import it.unisannio.orchestrator.model.CapabilityExecutionCommand;
import it.unisannio.orchestrator.service.CapabilityExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/capabilityExecutions")
@RequiredArgsConstructor
public class CapabilityExecutions {
    private final CapabilityExecutorService capabilityExecutorService;


    @PostMapping
    public ResponseEntity<Void> execute(@RequestBody CapabilityExecutionCommand command) {
        this.capabilityExecutorService.execute(command);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/results")
    public ResponseEntity<Void> results(@RequestBody Object results) {
        System.out.println(results);
        return ResponseEntity.noContent().build();
    }
}
