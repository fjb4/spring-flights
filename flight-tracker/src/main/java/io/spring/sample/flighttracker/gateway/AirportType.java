package io.spring.sample.flighttracker.gateway;

public enum AirportType {
    CIVILIAN {
        @Override
        public String toString() {
            return "CIVILIAN";

        }
    },
    MILITARY {
        @Override
        public String toString() {
            return "MILITARY";

        }
    }
}
