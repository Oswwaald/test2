package ca.polymtl.inf8480.tp2.shared;

import java.util.HashMap;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NameServiceInterface extends Remote {
	boolean verifyDispatcher(String username, String password) throws RemoteException;
	HashMap<String, Configuration> getCalculators(String username, String password) throws RemoteException;
	void setCalculator(String name, Configuration configuration) throws RemoteException ;
}
