package ca.polymtl.inf8480.tp2.nameService;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.HashMap;

import ca.polymtl.inf8480.tp2.shared.*;

public class NameService implements NameServiceInterface {
	
	private String usernameDispatcher;
	private String passwordDispatcher;
	private HashMap<CalculatorInterface, Integer> calculators;
	
    private NameService() {
    	calculators = new HashMap<CalculatorInterface, Integer>();
    }
    
    public static void main(String args[]) {
    	NameService nameService = new NameService();
    	nameService.run();
    }
    
    /*
	 * Mise en service du NameService pour la procédure RMI.
	 */
    private void run() {
		try {
			NameServiceInterface stub = (NameServiceInterface) UnicastRemoteObject.exportObject(this, 5000);
			Registry registry = LocateRegistry.getRegistry(5000);
			registry.rebind("nameservice", stub);
			System.out.println("NameService ready.");
		} catch (ConnectException e) {
			System.err.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lance ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
    }

	public boolean verifyDispatcher(String username, String password) {
		boolean verification = false;
		try
		{
			if (username == usernameDispatcher && password == passwordDispatcher) 
				verification = true;
			else
				verification = false;
		}
		catch (Exception e)
		{
			System.err.println("Erreur: " + e.getMessage());
		}
		return verification;
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
