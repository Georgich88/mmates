package com.georgeisaev.mmates.sherdog.parser;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan
@OpenAPIDefinition(
    info =
        @Info(
            title = "Mmates, sherdog collector",
            version = "1.0",
            description = "Documentation APIs v1.0"))
public class MmatesSherdogParserApplication {

  public static void main(String[] args) {
    SpringApplication.run(MmatesSherdogParserApplication.class, args);
  }
}
