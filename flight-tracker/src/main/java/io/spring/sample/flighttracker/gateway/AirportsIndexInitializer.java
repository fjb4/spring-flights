package io.spring.sample.flighttracker.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class AirportsIndexInitializer {

    private final Logger logger = LoggerFactory.getLogger(AirportsIndexInitializer.class);

    private final AirportsIndex airportsIndex;

    private final ObjectMapper objectMapper;

    public AirportsIndexInitializer(AirportsIndex airportsIndex, ObjectMapper objectMapper) {
        this.airportsIndex = airportsIndex;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void initializeAirportIndex() throws IOException {
        if (airportsIndex.size() == 0) {
            ClassPathResource airportsResource = new ClassPathResource("airports.json");
            AirportsFileEntry[] fileEntries = this.objectMapper
                    .readValue(airportsResource.getInputStream(), AirportsFileEntry[].class);

            airportsIndex.init(Arrays.stream(fileEntries).collect(Collectors.toMap(AirportsFileEntry::getCode, fileEntry -> new AirportIndexEntry(fileEntry.getType()))));

            logger.info("Loaded airports file with {} entries. Added {} airports to the index", fileEntries.length, airportsIndex.size());
        }
    }
}
