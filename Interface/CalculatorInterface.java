package ca.polymtl.inf8480.tp2.shared;

import java.util.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CalculatorInterface extends Remote {
	
	int calculate(List<String> operations, String username, String password) throws RemoteException;
}
