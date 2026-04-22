package com.reservationstudio.data;

import com.reservationstudio.model.Reservation;
import com.reservationstudio.model.Table;
import com.reservationstudio.model.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

//Open a CSV file
//Read it line by line
//Split each line by commas
//Turn each line into an object (User, Table, Reservation)
//Return a list of those objects

public class DataLoader {

    public static List<User> loadUsers(String filepath){
        List<User> users = new ArrayList<>();
        String line;
        boolean firstLine = true;


        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            while ((line = br.readLine()) != null){
                if(firstLine){
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                users.add(new User(parts[0], parts[1], User.parseRole(parts[2])));
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    public static List<Table> loadTables(String filepath){
        List<Table> tables = new ArrayList<>();
        String line;
        boolean firstLine = true;


        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            while ((line = br.readLine()) != null){
                if(firstLine){
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",");

                tables.add(new Table(
                        Integer.parseInt(parts[0].trim().replace("#", "")),
                        Integer.parseInt(parts[1].trim())
                ));
            }
        } catch (IOException e) {
            System.err.println("Error loading tables: " + e.getMessage());
        }
        return tables;
    }

    public static List<Reservation> loadReservations(String filepath){
        List<Reservation> reservations = new ArrayList<>();
        //String line;
        //boolean firstLine = true;

        //try (BufferedReader br = new BufferedReader(new FileReader(filepath)))
         //{
           // while ((line = br.readLine()) != null){
             //   if(firstLine){
               //     firstLine = false;
                 //   continue;
                //}
                //String[] parts = line.split(",");
                //reservations.add(new Reservation(Integer.parseInt(parts[0].trim().replace("#", "")), parts[1], Integer.parseInt(parts[2].trim().replace("#", "")), Integer.parseInt(parts[3]), parts[4], parts[5], Reservation.Status.valueOf(parts[6].trim())));
            //}
        //} catch (IOException e) {
            //System.err.println("Error loading reservations: " + e.getMessage());
       // }
        return reservations;
    }

}

