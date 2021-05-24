package com.parallel;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InvertedIndexInterface extends Remote {
    void CreateInvertedIndex() throws IOException;
}
