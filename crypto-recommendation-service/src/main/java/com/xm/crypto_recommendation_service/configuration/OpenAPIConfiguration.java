package com.xm.crypto_recommendation_service.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Crypto Recommendation",
                description = "Crypto recommendation API",
                contact = @Contact(
                        name = "Crypto Recommendation",
                        url = "https://cryptorecommendation.io",
                        email = "inbox@cryptorecommendation.io"
                )),
        servers = @Server(url = "http://localhost:8080")
)
public class OpenAPIConfiguration {

}
