package io.spring.sample.flighttracker.radars;

import io.spring.sample.flighttracker.gateway.AirportsIndex;
import org.springframework.cloud.gateway.rsocket.client.BrokerClient;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class RadarService {

	private final Mono<RSocketRequester> requesterMono;

	BrokerClient brokerClient;

	AirportsIndex airportsIndex;

	public RadarService(BrokerClient brokerClient, AirportsIndex airportsIndex) {
		this.airportsIndex = airportsIndex;
		this.brokerClient = brokerClient;
		this.requesterMono = brokerClient.connect().retry(5).cache();
		// Following line connects on startup
		// Otherwise will connect on first request
		this.requesterMono.subscribe();
	}

//      public RadarService(RSocketRequester.Builder builder, Environment env) {
//              String host = env.getProperty("radar-service.host", "localhost");
//              int port = env.getProperty("radar-service.port", Integer.class, 9898);
//              this.requesterMono = builder
//                              .dataMimeType(MediaType.APPLICATION_CBOR)
//                              .connectTcp(host, port).retry(5).cache();
//      }

	public Mono<AirportLocation> findRadar(String type, String code) {
		return this.requesterMono.flatMap(req ->
				req.route("find.radar.{type}.{code}", type, code)
						.metadata(brokerClient.forwarding(builder -> builder.serviceName("radar-collector")
								.with(airportsIndex.get(code).getTypeKey(), airportsIndex.get(code).getType())))
						.data(Mono.empty())
						.retrieveMono(AirportLocation.class));
	}

	// Leverages multi-casting feature in Gateway
	public Flux<AirportLocation> findRadars(ViewBox box, int maxCount) {
		return this.requesterMono
				.flatMapMany(req ->
						req.route("locate.radars.within")
								.metadata(brokerClient.forwarding(builder -> builder.serviceName("radar-collector")
										.with("multicast", "true")))
								.data(box)
								.retrieveFlux(AirportLocation.class))
				.take(maxCount);
	}

	public Flux<AircraftSignal> streamAircraftSignals(List<Radar> radars) {
		return this.requesterMono.flatMapMany(req ->
				Flux.fromIterable(radars).flatMap(radar ->
						req.route("listen.radar.{type}.{code}", radar.getType(), radar.getCode())
								.metadata(brokerClient.forwarding(builder -> builder.serviceName("radar-collector")
										.with(airportsIndex.get(radar.getCode()).getTypeKey(), airportsIndex.get(radar.getCode()).getType())))
								.data(Mono.empty())
								.retrieveFlux(AircraftSignal.class)));
	}

}