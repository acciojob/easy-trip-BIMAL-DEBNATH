package com.driver.controllers;


import com.driver.model.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class AirportController {

    HashMap<String,Airport>airportDB=new HashMap<>();
    HashMap<Integer, Flight>flightDB=new HashMap<>();
    HashMap<Integer, Passenger>passengerDB=new HashMap<>();
    HashMap<String,Pair>bookingDb=new HashMap<>();
   //***
    HashMap<Integer,Integer>revenueDB=new HashMap<>();



    @PostMapping("/add_airport")
    public String addAirport(@RequestBody Airport airport){

        //Simply add airport details to your database
        //Return a String message "SUCCESS"
        airportDB.put(airport.getAirportName(), airport);

        return "SUCCESS";
    }

    @GetMapping("/get-largest-aiport")
    public String getLargestAirportName(){

        //Largest airport is in terms of terminals. 3 terminal airport is larger than 2 terminal airport
        //Incase of a tie return the Lexicographically smallest airportName


        if(airportDB.isEmpty())return "";

        int largest=0;
        String name="";

        for(String Ar:airportDB.keySet()){

            if(airportDB.get(Ar).getNoOfTerminals()>largest){
                largest=airportDB.get(Ar).getNoOfTerminals();
                name=airportDB.get(Ar).getAirportName();
            } else if (airportDB.get(Ar).getNoOfTerminals()==largest){
                if(airportDB.get(Ar).getAirportName().compareTo(name)<0){
                    name=airportDB.get(Ar).getAirportName();
                    largest=airportDB.get(Ar).getNoOfTerminals();
                }
            }
        }
        if(!name.equals(""))return name;

        return null;
    }

    @GetMapping("/get-shortest-time-travel-between-cities")
    public double getShortestDurationOfPossibleBetweenTwoCities(@RequestParam("fromCity") City fromCity, @RequestParam("toCity")City toCity){

        //Find the duration by finding the shortest flight that connects these 2 cities directly
        //If there is no direct flight between 2 cities return -1.

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

    @GetMapping("/get-number-of-people-on-airport-on/{date}")
    public int getNumberOfPeopleOn(@PathVariable("date") Date date,@RequestParam("airportName")String airportName){

        //Calculate the total number of people who have flights on that day on a particular airport
        //This includes both the people who have come for a flight and who have landed on an airport after their flight

        int total = 0;

        City city=null;
        for(String portId:airportDB.keySet()){
            if(airportDB.get(portId).getAirportName().equals(airportName)){
                city=airportDB.get(portId).getCity();
            }
        }

        for(String key:bookingDb.keySet()){
            Pair temp=bookingDb.get(key);
            if(temp.getFlight().getFromCity().equals(city) || temp.getFlight().getToCity().equals(city)){
                if(temp.getFlight().getFlightDate().equals(date)){
                    total++;
                }
            }
        }
        return total;
    }

    @GetMapping("/calculate-fare")
    public int calculateFlightFare(@RequestParam("flightId")Integer flightId){

        //Calculation of flight prices is a function of number of people who have booked the flight already.
        //Price for any flight will be : 3000 + noOfPeopleWhoHaveAlreadyBooked*50
        //Suppose if 2 people have booked the flight already : the price of flight for the third person will be 3000 + 2*50 = 3100
        //This will not include the current person who is trying to book, he might also be just checking price

        int count=0;
        for(String key:bookingDb.keySet()){
            Pair tm=bookingDb.get(key);
            if(tm.getFlight().getFlightId()==flightId){
                count++;
            }
        }
        return 3000 + (count*50);

    }


    @PostMapping("/book-a-ticket")
    public String bookATicket(@RequestParam("flightId")Integer flightId,@RequestParam("passengerId")Integer passengerId){

        //If the numberOfPassengers who have booked the flight is greater than : maxCapacity, in that case :
        //return a String "FAILURE"
        //Also if the passenger has already booked a flight then also return "FAILURE".
        //else if you are able to book a ticket then return "SUCCESS"

        if(flightId==null || passengerId==null) return "FAILURE";

        int flightInt=flightId;
        int passengerInt=passengerId;
        String curr=String.valueOf(flightInt)+" "+String.valueOf(passengerInt);

        //System.out.println(flightInt+"  "+passengerInt);

        if(bookingDb.containsKey(curr)){
            return "FAILURE";
        }


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


       // return null;
    }

    @PutMapping("/cancel-a-ticket")
    public String cancelATicket(@RequestParam("flightId")Integer flightId,@RequestParam("passengerId")Integer passengerId){

        //If the passenger has not booked a ticket for that flight or the flightId is invalid or in any other failure case
        // then return a "FAILURE" message
        // Otherwise return a "SUCCESS" message
        // and also cancel the ticket that passenger had booked earlier on the given flightId

        int flightInt=flightId;
        int passengerInt=passengerId;
        String curr=String.valueOf(flightInt)+" "+String.valueOf(passengerInt);

        if(!bookingDb.containsKey(curr))return "FAILURE";


        int revenue=bookingDb.get(curr).getRevenueFromThisBooking();
        revenueDB.put(flightId,revenueDB.getOrDefault(flightId,0)-revenue);

        bookingDb.remove(curr);
        return "SUCCESS";

       // return null;
    }


    @GetMapping("/get-count-of-bookings-done-by-a-passenger/{passengerId}")
    public int countOfBookingsDoneByPassengerAllCombined(@PathVariable("passengerId")Integer passengerId){

        //Tell the count of flight bookings done by a passenger: This will tell the total count of flight bookings done by a passenger :
        int count=0;

        for(String key:bookingDb.keySet()){
            int team=bookingDb.get(key).getPassenger().getPassengerId();
            if(team==passengerId)count++;
        }
        return count;
        //return 0;
    }

    @PostMapping("/add-flight")
    public String addFlight(@RequestBody Flight flight){

        //Return a "SUCCESS" message string after adding a flight.
        if(!flightDB.containsKey(flight.getFlightId())) {
            flightDB.put(flight.getFlightId(), flight);
            return "SUCCESS";
        }
        return null;
    }


    @GetMapping("/get-aiportName-from-flight-takeoff/{flightId}")
    public String getAirportNameFromFlightId(@PathVariable("flightId")Integer flightId){

        //We need to get the starting airportName from where the flight will be taking off (Hint think of City variable if that can be of some use)
        //return null incase the flightId is invalid or you are not able to find the airportName

        if(!flightDB.containsKey(flightId))return null;


        City fromCityFlight=flightDB.get(flightId).getFromCity();
        for(String st:airportDB.keySet()){

            if (airportDB.get(st).getCity().equals(fromCityFlight)) {
                return  airportDB.get(st).getAirportName();
            }

        }
        return null;
    }


    @GetMapping("/calculate-revenue-collected/{flightId}")
    public int calculateRevenueOfAFlight(@PathVariable("flightId")Integer flightId){

        //Calculate the total revenue that a flight could have
        //That is of all the passengers that have booked a flight till now and then calculate the revenue
        //Revenue will also decrease if some passenger cancels the flight

        return revenueDB.getOrDefault(flightId, 0);

        //return 0;
    }


    @PostMapping("/add-passenger")
    public String addPassenger(@RequestBody Passenger passenger){

        //Add a passenger to the database
        //And return a "SUCCESS" message if the passenger has been added successfully.

        if(passenger==null )return null;
        passengerDB.put(passenger.getPassengerId(),passenger);
        return "SUCCESS";
        //return null;
    }


}
