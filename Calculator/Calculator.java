package ca.polymtl.inf8480.tp2.calculator;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.net.*;
import java.util.List;
import java.util.HashMap;


import ca.polymtl.inf8480.tp2.shared.*;

public class Calculator implements CalculatorInterface {
	
	private NameServiceInterface nameServiceStub;
	
	private static int calculatorCapacity;
	private static int calculatorErrorProb;
	
	public Calculator() {
		
		//TODO
		
	 }
	 
	 public static void main(String args[]) {
		//Enregistrement de la capacite et de la probabilité d'erreur en variables globales.
		calculatorCapacity = Integer.parseInt(args[0]);
		calculatorErrorProb = Integer.parseInt(args[1]);
		 
		// Lancement le serveur du Calculator
        Calculator calculator = new Calculator();
        calculator.run();
	 }
	 
	 /*
	  * Mise en service du Calculator pour la procédure RMI.
	  */
	 private void run() {
		try {
			System.setProperty("rmi.server.hostname", Inet4Address.getLocalHost().getHostName());
			CalculatorInterface stub = (CalculatorInterface) UnicastRemoteObject.exportObject(this, 5000);
			Registry registry = LocateRegistry.getRegistry(5000);
			registry.rebind("calculator", stub);
			System.out.println("Calculator ready.");
		} catch (ConnectException e) {
			System.err.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lance ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
		
		// Definition des informations du serveur de nameService.
	    // Hypothese de simplification : NameService a une adresse IP fixe (172.0.0.1).
		nameServiceStub = loadNameServiceStub("127.0.0.1");
	     
	    setCalculator(stub,calculatorCapacity);
	 }
	 
	public int calculate(List<String> operations, String username, String password) {
		double value = 0;
		
		if (nameServiceStub.verifyDispatcher(username, password))
		{
			float rate = (operations.size() - calculatorCapacity)/(4*calculatorCapacity);
			float randomNum = Math.random();
			if (rate <= randomNum)
			{
				// Diviser l'operande et l'operation en pair ?
				HashMap<String, Integer> ops = new HashMap<String, Integer>();
				ops = splitOperations(operations);
				for (HashMap<String, Integer> op : ops)
				{
					if(op.key == "pell")
						value = (value + Operations.pell(op.value)) %4000;
					else if(op.key == "prim")
						value = (value + Operations.prim(op.value)) %4000;
					else
					{
						//
						value = -3;
						break;
					}
				}
			}
			else
				//
				value = -1;
		}
		else
			//
			value = -2;
		return value;
	}
	
	public int getCalculatorCapacity() {
		return calculatorCapacity;
	}
	
	private HashMap<String, Integer> splitOperations(List<String> operations) {		
		HashMap<String, Integer> values = new HashMap<String, Integer>();
		foreach (String op in operations){
			String operation;
			int operande;
			String[] arguments = op.split(" ");
			operation = arguments[0];
			operande = Integer.parseInt(arguments[1]);
			values.put(operation, operande);

		}
		return values;
	}
	
	private NameServiceInterface loadNameServiceStub(String hostname) {
		NameServiceInterface stub = null;
		try {
			Registry registry = LocateRegistry.getRegistry(hostname,5000);
			stub = (NameServiceInterface) registry.lookup("nameservice");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

		return stub;
	}
}
