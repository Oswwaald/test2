package ca.polymtl.inf8480.tp2.calculator;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;
import java.util.*;

import ca.polymtl.inf8480.tp2.shared.*;

public class Calculator implements CalculatorInterface {
	
	private NameServiceInterface nameServiceStub;
	
	private static String calculatorIP;
	private static int calculatorCapacity;
	private static int calculatorErrorProb;
	private static int calculatorPort;
	private static String nameServiceIP;
	
	public Calculator() {
		
		//TODO
		
	 }
	 
	 public static void main(String args[]) {
		//Enregistrement de la capacite et de la probabilité d'erreur en variables globales.
		if (args.length == 5) {
			calculatorIP = args[0];
			calculatorCapacity = Integer.parseInt(args[1]);
			calculatorErrorProb = Integer.parseInt(args[2]);
			calculatorPort = Integer.parseInt(args[3]);
			nameServiceIP = args[4];
		} else {
			System.out.println("Le nombre d'argument est incorrect.");
			System.exit(1);
		}
		 
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
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
		
		// Definition des informations du serveur de nameService.
		nameServiceStub = loadNameServiceStub(nameServiceIP);
	    try {
	    	nameServiceStub.setCalculator(calculatorIP,calculatorCapacity,calculatorPort);
	    } catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	 }
	 
	public int calculate(List<String> operations, String username, String password) {
		double value = 0;
		
		if (nameServiceStub.verifyDispatcher(username, password))
		{
			float rate = (operations.size() - calculatorCapacity)/(4*calculatorCapacity);
			Random random = new Random();
			float randomNum = random.nextFloat();
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
						// Problème de syntaxe dans le fichier.
						value = -1;
						break;
					}
				}
			}
			else
				// Tache refusée car surcharge.
				value = -2;
		}
		else
			// Probleme d'identification dispatcher
			value = -3;
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
