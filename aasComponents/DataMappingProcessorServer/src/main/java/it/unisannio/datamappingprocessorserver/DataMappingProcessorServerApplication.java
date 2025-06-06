package it.unisannio.datamappingprocessorserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableMongoRepositories

public class DataMappingProcessorServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataMappingProcessorServerApplication.class, args);
    }

}
