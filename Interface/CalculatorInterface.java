package Interface;

import java.util.List;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CalculatorInterface extends Remote {
	
	int calculate(List<> operations, String username, String password) throws RemoteException;
	int getCalculatorCapacity() throws RemoteException;
}
