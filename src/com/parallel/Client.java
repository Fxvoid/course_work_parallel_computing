package com.parallel;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private Client() {
        ;
    }

    public static void main(String[] args) {
        try {

            Scanner in = new Scanner(System.in);
            // Getting the registry
            Registry registry = LocateRegistry.getRegistry(12345);
            // Looking up the registry for the remote object
            InvertedIndexInterface stub = (InvertedIndexInterface) registry.lookup("IndexInterface");


        } catch (Exception e) {
            System.err.println("Client exception: " + e);
            e.printStackTrace();
        }
    }
}
