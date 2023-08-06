package com.driver.controllers;

import com.driver.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;

@Repository
public class AirportRepository {


    HashMap<String,Airport>airportDB=new HashMap<>();
    HashMap<Integer, Flight>flightDB=new HashMap<>();
    HashMap<Integer, Passenger>passengerDB=new HashMap<>();
    HashMap<pair,Integer>bookingDb=new HashMap<>();

    HashMap<Integer,Integer>revenueDB=new HashMap<>();




    public String addAirport(Airport airport) {
        airportDB.put(airport.getAirportName(), airport);
         return "SUCCESS";
    }

    public String getLargestAirportName() {
        if(airportDB.size()==0)return null;

        int largest=0;
        String name="";

        for(String Ar:airportDB.keySet()){

            if(airportDB.get(Ar).getNoOfTerminals()>largest){
                largest=airportDB.get(Ar).getNoOfTerminals();
                name=airportDB.get(Ar).getAirportName();
            } else if (airportDB.get(Ar).getNoOfTerminals()==largest){
                if(airportDB.get(Ar).getAirportName().compareTo(name)>0){
                    name=airportDB.get(Ar).getAirportName();
                    largest=airportDB.get(Ar).getNoOfTerminals();
                }
            }
        }
        return name;
    }


    public String addFlight(Flight flight) {
        flightDB.put(flight.getFlightId(), flight);
        return "SUCCESS";
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity) {

        double ans=-1;

        for(int key:flightDB.keySet()){
            Flight curr=flightDB.get(key);

            if(curr.getFromCity().equals(fromCity) && curr.getToCity().equals(toCity)){
                if(ans==-1 || curr.getDuration()<ans){
                    ans=curr.getDuration();
                }
            }
        }
        return  ans;
    }


    public int getNumberOfPeopleOn(Date date, String airportName) {

        int total = 0;

        for(pair key:bookingDb.keySet()){
            String from=String.valueOf(key.getFlight().getFromCity());
            String tow=String.valueOf(key.getFlight().getToCity());
            if(from.equals(airportName) || tow.equals(airportName)){
                if(key.getFlight().getFlightDate()==date){
                    total++;
                }
            }
        }
        return total;
    }


    public int calculateFlightFare(Integer flightId) {

        int count=0;
        for(pair key:bookingDb.keySet()){
            if(key.getFlight().getFlightId()==flightId){
                count++;
            }
        }
        int pt=3000 + (count*50);
        return pt;
    }

    public String addPassenger(Passenger passenger) {

        passengerDB.put(passenger.getPassengerId(),passenger);
        return "SUCCESS";
    }

    public String bookATicket(Integer flightId, Integer passengerId) {

        Flight ft=flightDB.getOrDefault(flightId, null);
        Passenger pt=passengerDB.getOrDefault(passengerId, null);

        if(ft==null || pt==null)return "FAILURE";

        pair curr=new pair(pt,ft);

        if(bookingDb.containsKey(curr)){
            return "FAILURE";
        }

        int count=0;

        for(pair key:bookingDb.keySet()){

           if(key.getFlight().getFlightId()==flightId)count++;
        }

        if(count>=flightDB.get(flightId).getMaxCapacity())return "FAILURE";

        int revenue=calculateFlightFare(flightId);
        revenueDB.put(flightId,revenueDB.getOrDefault(flightId,0)+revenue);
        bookingDb.put(curr, revenue);

        return "SUCCESS";

    }


    public String cancelATicket(Integer flightId, Integer passengerId) {

        if(!flightDB.containsKey(flightId) || !passengerDB.containsKey(passengerId))return "FAILURE";
        pair curr=new pair(passengerDB.get(passengerId),flightDB.get(flightId));

        if(!bookingDb.containsKey(curr))return "FAILURE";


        int revenue=bookingDb.get(curr);
        revenueDB.put(flightId,revenueDB.getOrDefault(flightId,0)-revenue);

          bookingDb.remove(curr);
            return "SUCCESS";

    }

    public String getAirportNameFromFlightId(Integer flightId) {

        if(!flightDB.containsKey(flightId))return null;
        String team=null;

        City fromCityFlight=flightDB.get(flightId).getFromCity();
        for(String st:airportDB.keySet()){

            if (airportDB.get(st).getCity().equals(fromCityFlight)) {
                team=airportDB.get(st).getAirportName();
            }

        }


        return team;
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId) {

        int count=0;

        for(pair key:bookingDb.keySet()){
            if(key.getPassenger().getPassengerId()==passengerId)count++;
        }
        return count;
    }

    public int calculateRevenueOfAFlight(Integer flightId) {
        return revenueDB.getOrDefault(flightId, 0);
    }
}
