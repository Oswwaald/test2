package Interface;

import java.util.HashMap;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NameServiceInterface extends Remote {
	boolean verifyDispatcher(String username, String password) throws RemoteException;
	HashMap<CalculatorInterface, Integer> getCalculators() throws RemoteException;
	HashMap<String, Integer> setCalculator(String computerID, int capacity);
}
