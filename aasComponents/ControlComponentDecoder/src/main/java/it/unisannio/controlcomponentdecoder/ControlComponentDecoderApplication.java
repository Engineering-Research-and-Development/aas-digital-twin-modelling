package it.unisannio.controlcomponentdecoder;

import it.unisannio.controlcomponentdecoder.service.ControlComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ControlComponentDecoderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ControlComponentDecoderApplication.class, args);
    }

}
