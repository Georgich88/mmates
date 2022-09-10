package com.georgeisaev.mmates.sherdog.parser;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

public class MmatesSherdogParserApplication {

    public static void main(String[] args) {
        SpringApplication.run(MmatesSherdogParserApplication.class, args);
    }

}
