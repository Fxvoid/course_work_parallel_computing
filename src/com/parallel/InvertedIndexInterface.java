package com.parallel;

import java.io.IOException;
import java.nio.file.Path;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface InvertedIndexInterface extends Remote {
    void createInvertedIndex() throws IOException;
    String search(String search_query) throws RemoteException;
}
