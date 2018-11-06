package ca.polymtl.inf8480.tp2.shared;

import java.util.ArrayList;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NameServiceInterface extends Remote {
	boolean verifyDispatcher(String username, String password) throws RemoteException;
	ArrayList<Configuration> getCalculators(String username, String password) throws RemoteException;
	void setCalculator(Configuration configuration) throws RemoteException ;
}
