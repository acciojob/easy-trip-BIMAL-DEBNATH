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
    HashMap<String,Pair>bookingDb=new HashMap<>();

    HashMap<Integer,Integer>revenueDB=new HashMap<>();




    public String addAirport(Airport airport) {
        airportDB.put(airport.getAirportName(), airport);
         return "SUCCESS";
    }

    public String getLargestAirportName() {
        if(airportDB.isEmpty())return "";

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

          City city=null;
        for(String portId:airportDB.keySet()){
            if(portId.equals(airportName)){
                city=airportDB.get(portId).getCity();
            }
        }

        for(String key:bookingDb.keySet()){
            Pair temp=bookingDb.get(key);
            if(temp.getFlight().getFromCity().equals(city) || temp.getFlight().getToCity().equals(city)){
                if(temp.getFlight().getFlightDate()==date){
                    total++;
                }
            }
        }
        return total;
    }


    public int calculateFlightFare(Integer flightId) {

        int count=0;
        for(String key:bookingDb.keySet()){
            Pair tm=bookingDb.get(key);
            if(tm.getFlight().getFlightId()==flightId){
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

        int flightInt=flightId;
        int passengerInt=passengerId;
        String curr=String.valueOf(flightInt)+" "+String.valueOf(passengerInt);

        //System.out.println(flightInt+"  "+passengerInt);

        if(bookingDb.containsKey(curr)){
            return "FAILURE";
        }

        //System.out.println(bookingDb.getOrDefault(curr,0)+" revenue");
        //System.out.println(flag);

        int count=0;

        for(String key:bookingDb.keySet()){
            Pair team=bookingDb.get(key);
           if(team.getFlight().getFlightId()==flightId)count++;
        }

        if(count>=flightDB.get(flightId).getMaxCapacity())return "FAILURE";

        int revenue=calculateFlightFare(flightId);
        revenueDB.put(flightId,revenueDB.getOrDefault(flightId,0)+revenue);
        bookingDb.put(curr, new Pair(passengerDB.get(passengerId),flightDB.get(flightId),revenue));

        return "SUCCESS";

    }


    public String cancelATicket(Integer flightId, Integer passengerId) {

        if(!flightDB.containsKey(flightId) || !passengerDB.containsKey(passengerId))return "FAILURE";

        int flightInt=flightId;
        int passengerInt=passengerId;
        String curr=String.valueOf(flightInt)+" "+String.valueOf(passengerInt);

        if(!bookingDb.containsKey(curr))return "FAILURE";


        int revenue=bookingDb.get(curr).getRevenueFromThisBooking();
        revenueDB.put(flightId,revenueDB.getOrDefault(flightId,0)-revenue);

          bookingDb.remove(curr);
            return "SUCCESS";

    }

    public String getAirportNameFromFlightId(Integer flightId) {

        if(!flightDB.containsKey(flightId))return null;


        City fromCityFlight=flightDB.get(flightId).getFromCity();
        for(String st:airportDB.keySet()){

            if (airportDB.get(st).getCity().equals(fromCityFlight)) {
                return  airportDB.get(st).getAirportName();
            }

        }


        return null;
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId) {

        int count=0;

        for(String key:bookingDb.keySet()){
            int team=bookingDb.get(key).getPassenger().getPassengerId();
            if(team==passengerId)count++;
        }
        return count;
    }

    public int calculateRevenueOfAFlight(Integer flightId) {
        return revenueDB.getOrDefault(flightId, 0);
    }
}
