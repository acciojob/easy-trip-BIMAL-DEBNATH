package com.driver.model;

public class pair {

    Passenger passenger;
    Flight flight;

    public pair(Passenger p, Flight f){
        this.passenger=p;
        this.flight=f;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }
}
