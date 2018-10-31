package NameService;
import java.rmi.RemoteException;
import java.util.HashMap;

import Interface.CalculatorInterface;

public class NameService implements NameServiceInterface {
	
	private String usernameDispatcher;
	private String passwordDispatcher;
	private HashMap<CalculatorInterface, Integer> calculators;
	
    private NameService() {
    	calculators = new HashMap<CalculatorInterface, Integer>();
    }
    
    public static void main(String args[]) {
    	NameService ns = new NameService();
    	ns.run();
    }
    
    private void run() {
    	//TODO
    }
    
	public boolean verifyDispatcher(String username, String password) {
		try
		{
			if (username == usernameDispatcher && password == passwordDispatcher) 
				return true;
			else
				return false;
		}
		
		catch (Exception e)
		{
			System.err.println("Erreur: " + e.getMessage());
		}	
	}
	
	public HashMap<CalculatorInterface, Integer> getCalculators(String username, String password){
		usernameDispatcher = username;
		passwordDispatcher = password;
		return calculators;
		
	}
	public void setCalculator(CalculatorInterface calculator, int capacity){
		calculators.putIfAbsent(calculator, capacity);
	}
}
