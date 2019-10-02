package io.spring.sample.flighttracker.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AirportsIndex {

    private final Logger logger = LoggerFactory.getLogger(AirportsIndex.class);

    private Map<String, AirportIndexEntry> airportsIndex;

    public AirportsIndex() {
        this.airportsIndex = new HashMap<String, AirportIndexEntry>();
    }

    public void init(Map<String, AirportIndexEntry> airportsIndex) {
        this.airportsIndex = airportsIndex;
    }

    public AirportIndexEntry get(String code) {
        return airportsIndex.get(code);
    }

    public int size() { return airportsIndex.size(); }

}