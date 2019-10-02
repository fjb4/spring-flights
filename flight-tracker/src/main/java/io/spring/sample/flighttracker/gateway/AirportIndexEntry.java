package io.spring.sample.flighttracker.gateway;

import org.springframework.cloud.gateway.rsocket.common.metadata.WellKnownKey;

public class AirportIndexEntry {

	private WellKnownKey typeKey = WellKnownKey.INSTANCE_NAME;

	private Enum<AirportType> type;

	public AirportIndexEntry(String type) {
		this.type = AirportType.valueOf(type);
	}

	public WellKnownKey getTypeKey() {
		return typeKey;
	}

	public String getType() {
		return type.toString();
	}

	@Override
	public String toString() {
		return "AirportIndexEntry{" +
				"typeKey=" + typeKey +
				" (INSTANCE_NAME), type='" + type +
				"'}";
	}

}
