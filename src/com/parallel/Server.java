package com.parallel;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {

    public Server() {
        ;
    }

    public static void main(String args[]) {
        try {
            // Instantiating the implementation class
            InvertedIndexManager obj = new InvertedIndexManager();

            // Exporting the object of implementation class
            // (here we are exporting the remote object)
            InvertedIndexInterface stub = (InvertedIndexInterface) UnicastRemoteObject.exportObject(obj, 0);

            // Binding the remote object in the registry
            LocateRegistry.createRegistry(12345);
            Registry registry = LocateRegistry.getRegistry(12345);

            registry.bind("IndexInterface", stub);

            stub.createInvertedIndex();
            System.out.println("Done creating inverted index!");

        } catch (Exception e) {
            System.err.println("Server exception: " + e);
            e.printStackTrace();
        }
    }
}
