package ca.polymtl.inf8480.tp2.shared;

import java.util.HashMap;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NameServiceInterface extends Remote {
	boolean verifyDispatcher(String username, String password) throws RemoteException;
	HashMap<CalculatorInterface, Integer> getCalculators(String username, String password) throws RemoteException;
	void setCalculator(CalculatorInterface calculator, int capacity) throws RemoteException ;
}
