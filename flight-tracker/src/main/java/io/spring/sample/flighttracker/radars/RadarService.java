package io.spring.sample.flighttracker.radars;

import io.spring.sample.flighttracker.gateway.AirportsIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.rsocket.client.BrokerClient;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class RadarService {

	private final Logger logger = LoggerFactory.getLogger(RadarService.class);

	private RSocketRequester rSocketRequester;

	BrokerClient brokerClient;

	AirportsIndex airportsIndex;

	public RadarService(BrokerClient brokerClient, AirportsIndex airportsIndex) {
		this.airportsIndex = airportsIndex;
		this.brokerClient = brokerClient;
	}

	@EventListener
	public void getClient(PayloadApplicationEvent<RSocketRequester> event) {
		logger.info("Payload event: getting rSocketRequester event paylaod: {}", event.getPayload().hashCode());
		this.rSocketRequester = event.getPayload();
	}

	public Mono<AirportLocation> findRadar(String type, String code) {
		return rSocketRequester.route("find.radar.{type}.{code}", type, code)
			.metadata(brokerClient.forwarding(builder -> builder.serviceName("radar-collector")
					.with(airportsIndex.get(code).getTypeKey(), airportsIndex.get(code).getType())))
			.data(Mono.empty())
			.retrieveMono(AirportLocation.class);
	}

	// Leverages multi-casting feature in Gateway
	public Flux<AirportLocation> findRadars(ViewBox box, int maxCount) {
		return rSocketRequester.route("locate.radars.within")
			.metadata(brokerClient.forwarding(builder -> builder.serviceName("radar-collector")
					.with("multicast", "true")))
			.data(box)
			.retrieveFlux(AirportLocation.class)
		.take(maxCount);
	}

	public Flux<AircraftSignal> streamAircraftSignals(List<Radar> radars) {
		return Flux.fromIterable(radars).flatMap(radar ->
				rSocketRequester.route("listen.radar.{type}.{code}", radar.getType(), radar.getCode())
						.metadata(brokerClient.forwarding(builder -> builder.serviceName("radar-collector")
								.with(airportsIndex.get(radar.getCode()).getTypeKey(), airportsIndex.get(radar.getCode()).getType())))
						.data(Mono.empty())
						.retrieveFlux(AircraftSignal.class));
	}

}