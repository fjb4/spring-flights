package io.spring.sample.radarcollector.gateway;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.gateway.rsocket.client.BrokerClient;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

//@Profile("gateway")
@Component
public class GatewayClient {

    private BrokerClient brokerClient;

    public GatewayClient(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
    }

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Make a blocking connection to Gateway
        brokerClient.connect().block();
    }
}
