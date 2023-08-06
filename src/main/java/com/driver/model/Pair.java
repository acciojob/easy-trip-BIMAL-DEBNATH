package com.driver.model;

public class Pair {
    Passenger passenger;
    Flight flight;
    int revenueFromThisBooking;


    public Pair(Passenger passenger, Flight flight, int revenueFromThisBooking) {
        this.passenger = passenger;
        this.flight = flight;
        this.revenueFromThisBooking = revenueFromThisBooking;
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

    public int getRevenueFromThisBooking() {
        return revenueFromThisBooking;
    }

    public void setRevenueFromThisBooking(int revenueFromThisBooking) {
        this.revenueFromThisBooking = revenueFromThisBooking;
    }
}
